package com.payment.ingestor.repository.impl;

import com.payment.ingestor.dto.account.RecipientAccountResponse;
import com.payment.ingestor.dto.account.RecipientAccountSearchCriteria;
import com.payment.ingestor.entity.Account;
import com.payment.ingestor.entity.User;
import com.payment.ingestor.exception.AppIllegalArgumentException;
import com.payment.ingestor.model.AccountStatus;
import com.payment.ingestor.repository.RecipientAccountQueryRepository;
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
public class RecipientAccountQueryRepositoryImpl implements RecipientAccountQueryRepository {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            USER_NAME_FIELD,
            ACCOUNT_NAME_FIELD,
            ACCOUNT_TYPE_FIELD,
            ACCOUNT_CURRENCY_FIELD
    );

    private final EntityManager entityManager;

    @Override
    public Page<RecipientAccountResponse> findRecipientAccounts(RecipientAccountSearchCriteria criteria, Pageable pageable) {

        List<RecipientAccountResponse> content = findContent(criteria, pageable);
        long total = countRecipients(criteria);

        return new PageImpl<>(content, pageable, total);
    }

    private List<RecipientAccountResponse> findContent(RecipientAccountSearchCriteria criteria, Pageable pageable) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<RecipientAccountResponse> query = cb.createQuery(RecipientAccountResponse.class);
        Root<Account> account = query.from(Account.class);

        Join<Account, User> user = account.join(USER_FIELD, JoinType.INNER);
        List<Predicate> predicates = buildPredicates(criteria, cb, account, user);
        query.select(
                cb.construct(
                        RecipientAccountResponse.class,
                        account.get(ACCOUNT_ID_FIELD),
                        user.get(ID_FIELD),
                        buildUserName(cb, user),
                        user.get(EMAIL_FIELD),
                        account.get(ACCOUNT_NAME_FIELD),
                        account.get(ACCOUNT_TYPE_FIELD),
                        account.get(ACCOUNT_CURRENCY_FIELD)
                )
        );

        query.where(predicates.toArray(new Predicate[0]));
        applySorting(query, cb, account, user, pageable);

        TypedQuery<RecipientAccountResponse> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        return typedQuery.getResultList();
    }

    private List<Predicate> buildPredicates(
            RecipientAccountSearchCriteria criteria,
            CriteriaBuilder cb,
            Root<Account> account,
            Join<Account, User> user
    ) {

        List<Predicate> predicates = new ArrayList<>();

        // Exclude accounts belonging to the currently authenticated user
        predicates.add(cb.notEqual(account.get(USER_FIELD).get(ID_FIELD), criteria.currentUserId()));

        // Only ACTIVE accounts should appear as payment recipients
        predicates.add(cb.equal(account.get(ACCOUNT_STATUS_FIELD), AccountStatus.ACTIVE));

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

        // Account type filter
        if (criteria.accountType() != null) {
            predicates.add(cb.equal(account.get(ACCOUNT_TYPE_FIELD), criteria.accountType()));
        }

        //Currency filter
        if (criteria.currency() != null && !criteria.currency().isBlank()) {
            predicates.add(cb.equal(cb.upper(account.get(ACCOUNT_CURRENCY_FIELD)), criteria.currency().trim().toUpperCase(Locale.ROOT)));
        }

        return predicates;
    }

    private Expression<String> buildUserName(CriteriaBuilder cb, Join<Account, User> user) {

        return cb.concat(cb.concat(user.get(FIRST_NAME_FIELD), " "), user.get(LAST_NAME_FIELD));
    }

    private long countRecipients(RecipientAccountSearchCriteria criteria) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Account> account = countQuery.from(Account.class);
        Join<Account, User> user = account.join(USER_FIELD, JoinType.INNER);
        List<Predicate> predicates = buildPredicates(criteria, cb, account, user);
        countQuery.select(cb.count(account));
        countQuery.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    private void applySorting(
            CriteriaQuery<RecipientAccountResponse> query,
            CriteriaBuilder cb,
            Root<Account> account,
            Join<Account, User> user,
            Pageable pageable
    ) {

        List<Order> orders = new ArrayList<>();
        if (pageable.getSort().isUnsorted()) {
            orders.add(cb.asc(buildUserName(cb, user)));
            orders.add(cb.asc(account.get(ACCOUNT_ID_FIELD)));
            query.orderBy(orders);
            return;
        }

        for (Sort.Order sortOrder : pageable.getSort()) {
            String field = sortOrder.getProperty();
            if (!ALLOWED_SORT_FIELDS.contains(field)) {
                throw new AppIllegalArgumentException(
                        field,
                        UNSUPPORTED_SORT_FIELD + field
                );
            }

            Expression<?> expression;
            if (USER_NAME_FIELD.equals(field)) {
                expression = buildUserName(cb, user);
            } else {
                expression = account.get(field);
            }

            if (sortOrder.isAscending()) {
                orders.add(cb.asc(expression));
            } else {
                orders.add(cb.desc(expression));
            }
        }

        //Stable pagination.
        orders.add(cb.asc(account.get(ACCOUNT_ID_FIELD)));
        query.orderBy(orders);
    }

}
