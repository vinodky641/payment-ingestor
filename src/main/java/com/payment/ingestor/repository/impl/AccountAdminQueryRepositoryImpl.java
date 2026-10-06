package com.payment.ingestor.repository.impl;

import com.payment.ingestor.dto.account.AdminAccountResponse;
import com.payment.ingestor.dto.account.AdminAccountSearchCriteria;
import com.payment.ingestor.entity.Account;
import com.payment.ingestor.entity.User;
import com.payment.ingestor.exception.AppIllegalArgumentException;
import com.payment.ingestor.repository.AccountAdminQueryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.payment.ingestor.constant.PaymentIngestorConstants.*;

@Repository
@RequiredArgsConstructor
public class AccountAdminQueryRepositoryImpl implements AccountAdminQueryRepository {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            ACCOUNT_ID_FIELD,
            ACCOUNT_NAME_FIELD,
            ACCOUNT_TYPE_FIELD,
            USER_NAME_FIELD,
            ACCOUNT_BALANCE_FIELD,
            ACCOUNT_STATUS_FIELD,
            ACCOUNT_CURRENCY_FIELD,
            ACCOUNT_OPENED_DATE_FIELD
    );

    private final EntityManager entityManager;

    @Override
    public Page<AdminAccountResponse> findAccountsForAdminView(AdminAccountSearchCriteria criteria, Pageable pageable) {

        List<AdminAccountResponse> content = findContent(criteria, pageable);
        long total = countAccounts(criteria);

        return new PageImpl<>(content, pageable, total);
    }

    private List<AdminAccountResponse> findContent(AdminAccountSearchCriteria criteria, Pageable pageable) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AdminAccountResponse> query = cb.createQuery(AdminAccountResponse.class);
        Root<Account> account = query.from(Account.class);
        Join<Account, User> user = account.join(USER_FIELD, JoinType.INNER);

        List<Predicate> predicates = buildPredicates(criteria, cb, account, user);
        query.select(
                cb.construct(
                        AdminAccountResponse.class,
                        account.get(ACCOUNT_ID_FIELD),
                        user.get(ID_FIELD),
                        buildUserName(cb, user),
                        user.get(EMAIL_FIELD),
                        account.get(ACCOUNT_NAME_FIELD),
                        account.get(ACCOUNT_TYPE_FIELD),
                        account.get(ACCOUNT_STATUS_FIELD),
                        account.get(ACCOUNT_BALANCE_FIELD),
                        account.get(ACCOUNT_CURRENCY_FIELD),
                        account.get(ACCOUNT_OPENED_DATE_FIELD)
                )
        );

        query.where(predicates.toArray(new Predicate[0]));
        applySorting(query, cb, account, user, pageable);

        TypedQuery<AdminAccountResponse> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        return typedQuery.getResultList();
    }

    private long countAccounts(AdminAccountSearchCriteria criteria) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Account> account = countQuery.from(Account.class);

        Join<Account, User> user = account.join(USER_FIELD, JoinType.INNER);
        List<Predicate> predicates = buildPredicates(criteria, cb, account, user);
        countQuery.select(cb.count(account));
        countQuery.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    private List<Predicate> buildPredicates(
            AdminAccountSearchCriteria criteria,
            CriteriaBuilder cb,
            Root<Account> account,
            Join<Account, User> user
    ) {
        List<Predicate> predicates = new ArrayList<>();
        // Search
        if (criteria.search() != null && !criteria.search().isBlank()) {
            String search = "%" + criteria.search().trim().toLowerCase(Locale.ROOT) + "%";
            Predicate accountId = cb.like(cb.lower(account.get(ACCOUNT_ID_FIELD)), search);
            Predicate accountName = cb.like(cb.lower(account.get(ACCOUNT_NAME_FIELD)), search);
            Predicate firstName = cb.like(cb.lower(user.get(FIRST_NAME_FIELD)), search);
            Predicate lastName = cb.like(cb.lower(user.get(LAST_NAME_FIELD)), search);
            Predicate email = cb.like(cb.lower(user.get(EMAIL_FIELD)), search);
            predicates.add(cb.or(accountId, accountName, firstName, lastName, email));
        }

        // Status
        if (criteria.status() != null) {
            predicates.add(cb.equal(account.get(ACCOUNT_STATUS_FIELD), criteria.status()));
        }

        // Account type
        if (criteria.accountType() != null) {
            predicates.add(cb.equal(account.get(ACCOUNT_TYPE_FIELD), criteria.accountType()));
        }

        return predicates;
    }

    private Expression<String> buildUserName(CriteriaBuilder cb, Join<Account, User> user) {

        return cb.concat(cb.concat(user.get(FIRST_NAME_FIELD), " "), user.get(LAST_NAME_FIELD));
    }

    private void applySorting(
            CriteriaQuery<AdminAccountResponse> query,
            CriteriaBuilder cb,
            Root<Account> account,
            Join<Account, User> user,
            Pageable pageable
    ) {

        List<Order> orders = new ArrayList<>();
        if (pageable.getSort().isUnsorted()) {
            orders.add(cb.desc(account.get(ACCOUNT_OPENED_DATE_FIELD)));
            orders.add(cb.asc(account.get(ACCOUNT_ID_FIELD)));
            query.orderBy(orders);
            return;
        }

        for (Sort.Order sortOrder : pageable.getSort()) {
            String requestedField = sortOrder.getProperty();
            if (!ALLOWED_SORT_FIELDS.contains(requestedField)) {
                throw new AppIllegalArgumentException(
                        requestedField,
                        UNSUPPORTED_SORT_FIELD + requestedField
                );
            }

            Expression<?> expression;
            if (USER_NAME_FIELD.equals(requestedField)) {
                expression = buildUserName(cb, user);
            } else {
                expression = account.get(requestedField);
            }

            if (sortOrder.isAscending()) {
                orders.add(cb.asc(expression));
            } else {
                orders.add(cb.desc(expression));
            }

        }

        // If two records have the same openedDate, accountId provides deterministic ordering
        orders.add(cb.asc(account.get(ACCOUNT_ID_FIELD)));
        query.orderBy(orders);
    }

}
