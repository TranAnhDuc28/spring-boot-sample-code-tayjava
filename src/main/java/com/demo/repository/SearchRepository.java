package com.demo.repository;

import com.demo.dto.reponse.PageResponse;
import com.demo.model.Address;
import com.demo.model.User;
import com.demo.repository.criteria.SearchCriteria;
import com.demo.repository.criteria.UserSearchCriteriaQueryConsumor;
import com.demo.repository.specification.SpecSearchCriteria;
import com.demo.util.AppConst;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class SearchRepository {

    /**
     * - @PersistenceContext là một annotation hữu ích trong JPA để inject EntityManager vào các bean
     * quản lý bởi container, giúp dễ dàng thực hiện các thao tác với cơ sở dữ liệu một cách tiện lợi và hiệu quả.
     */
    @PersistenceContext
    private EntityManager entityManager;

    private static final String LIKE_FORMAT = "%%%s%%";

    /**
     * @param pageNo
     * @param pageSize
     * @param search with field firstName, lastName, email
     * @param sortBy
     * @return user list with sorting and paging
     */
    public PageResponse<?> getAllUsersWithSortByColumnAndSearch(int pageNo, int pageSize, String search, String sortBy) {
            log.info("Execute search user with keyword={}", search);
        int pageNumber = 0;
        if (pageNo > 0) pageNumber = pageNo - 1;

        /**
         * - StringBuilder không đồng bộ (Non-synchronized) nó không an toàn trong môi trường đa luồng (multi-threaded).
         * Nếu cần sử dụng nó trong một môi trường đa luồng, nên sử dụng StringBuffer, lớp này tương tự như
         * StringBuilder nhưng đồng bộ.
         * - Hiệu suất cao hơn String và StringBuffer, vì StringBuilder không đồng bộ. Cả StringBuilder và StringBuffer
         * đều hiệu quả hơn String khi thực hiện nhiều thao tác nối chuỗi.
         * - Các đối tượng StringBuilder có thể thay đổi, có nghĩa là các phương thức của nó có thể thay đổi
         * nội dung của đối tượng mà không tạo ra một đối tượng mới.
         */
        StringBuilder sqlQuery = new StringBuilder(
                "SELECT new com.demo.dto.reponse.UserDetailResponse(u.id, u.firstName, u.lastName, u.phone, u.email) FROM User u WHERE 1=1");
        if (StringUtils.hasLength(search)) {
            sqlQuery.append(" AND lower(u.firstName) like lower(:firstName)");
            sqlQuery.append(" OR lower(u.lastName) like lower(:lastName)");
            sqlQuery.append(" OR lower(u.email) like lower(:email)");
        }

        if (StringUtils.hasLength(sortBy)) {
            // sortBy -> firstName:asc|desc
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)"); // trong dấu () là 1 group
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                sqlQuery.append(String.format(" order by u.%s %s", matcher.group(1), matcher.group(3)));
            }
        }

        // Query ra list user
        Query selectQuery = entityManager.createQuery(sqlQuery.toString());
        if (StringUtils.hasLength(search)) {
            selectQuery.setParameter("firstName", String.format(LIKE_FORMAT, search)); // %%%s%% -> %search%
            selectQuery.setParameter("lastName", String.format(LIKE_FORMAT, search));
            selectQuery.setParameter("email", String.format(LIKE_FORMAT, search));
        }
        int offset = pageNumber * pageSize;
        selectQuery.setFirstResult(offset);
        selectQuery.setMaxResults(pageSize);
        List<?> users = selectQuery.getResultList();

        // count users
        StringBuilder sqlCountQuery = new StringBuilder("SELECT COUNT(*) FROM User u");
        if (StringUtils.hasLength(search)) {
            sqlCountQuery.append(" WHERE lower(u.firstName) like lower(?1)");
            sqlCountQuery.append(" OR lower(u.lastName) like lower(?2)");
            sqlCountQuery.append(" OR lower(u.email) like lower(?3)");
        }

        Query countQuery = entityManager.createQuery(sqlCountQuery.toString());
        if (StringUtils.hasLength(search)) {
            countQuery.setParameter(1, String.format(LIKE_FORMAT, search)); // %%%s%% -> %search%
            countQuery.setParameter(2, String.format(LIKE_FORMAT, search));
            countQuery.setParameter(3, String.format(LIKE_FORMAT, search));
        }
        Long totalElements = (Long) countQuery.getSingleResult();
        log.info("totalElements={}", totalElements);

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<?> page = new PageImpl<>(users, pageable, totalElements);

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(page.getTotalPages())
                .items(page.stream().toList())
                .build();
    }

    /**
     * Advance search user by criterias
     *
     * @param pageNo
     * @param pageSize
     * @param sortBy value of multiple columns
     * @param address default field city in table tbl_address
     * @param search multiple columns
     * @return
     */
    public PageResponse<?> searchUserByCriteria(int pageNo, int pageSize, String sortBy, String address, String... search) {
        log.info("Search user with search={}, address={} sortBy={}", search, address, sortBy);

        // search -> firstName:T, lastName:T
        List<SearchCriteria> criteriaList = new ArrayList<>();

        //  Lấy danh sách user
        if (search.length > 0) {
            Pattern pattern = Pattern.compile(AppConst.SEARCH_OPERATOR); // trong dấu () là 1 group
            for (String s : search) {
                // giá trị đúng -> firstName:value
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    criteriaList.add(new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
                }
            }
        }

        List<User> users = getUsers(pageNo, pageSize, criteriaList, sortBy, address);

        // 2. lấy ra số lượng bản ghi
        Long totalElements = getTotalElements(criteriaList, address);

        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
//        Page<User> page = new PageImpl<>(users, PageRequest.of(pageNo, pageSize), totalElements);

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(totalPages)
                .items(users)
                .build();
    }

    /**
     * Get all users by condition
     *
     * @param pageNo
     * @param pageSize
     * @param criteriaList value of multiple columns
     * @param sortBy multiple columns
     * @param address default field city in table tbl_address
     * @return
     */
    private List<User> getUsers(int pageNo, int pageSize, List<SearchCriteria> criteriaList, String sortBy, String address) {
        log.info("-------------- getUsers --------------");
        int pageNumber = 0;
        if (pageNo > 0) pageNumber = pageNo - 1;

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);
        Root<User> userRoot = query.from(User.class); // chỉ định vào đối tượng ta sẽ tìm kiếm

        // Xử lý các điều kiện tìm kiếm
        /**
         * - Predicate là một phần của Criteria API để xây dựng các truy vấn động
         * - phương thức conjunction() của CriteriaBuilder trả về một Predicate biểu diễn điều kiện "AND"
         * không có bất kỳ điều kiện nào kèm theo.
         * - Sử dụng conjunction() thường là điểm khởi đầu cho việc xây dựng một loạt
         * các điều kiện mà ta có thể nối vào nhau.
         *
         */
        Predicate userPredicate = criteriaBuilder.conjunction();
        UserSearchCriteriaQueryConsumor searchConsumer = new UserSearchCriteriaQueryConsumor(userPredicate, criteriaBuilder, userRoot);

        // join bảng address nếu truyền vào giá trị tìm kiếm address
        if (StringUtils.hasLength(address)) {
            Join<Address, User> userAddressJoin = userRoot.join("addresses"); // Address join User
            // thông qua trường nào, giá trị nào để đối chiếu
            Predicate addressPredicate = criteriaBuilder.like(criteriaBuilder.lower(userAddressJoin.get("city")),
                    String.format(LIKE_FORMAT, address.toLowerCase()));
            // thêm vào điều kiện truy vấn
            criteriaList.forEach(searchConsumer);
            userPredicate = searchConsumer.getPredicate();
            query.where(userPredicate, addressPredicate);
        } else {
            criteriaList.forEach(searchConsumer);
            userPredicate = searchConsumer.getPredicate();
            query.where(userPredicate);
        }

        // sort
        Pattern pattern = Pattern.compile(AppConst.SORT_BY); // trong dấu () là 1 group
        if (StringUtils.hasLength(sortBy)) {
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                String fieldName = matcher.group(1);
                String direction = matcher.group(3);
                if (direction.equalsIgnoreCase("asc")) {
                    query.orderBy(criteriaBuilder.asc(userRoot.get(fieldName)));
                } else {
                    query.orderBy(criteriaBuilder.desc(userRoot.get(fieldName)));
                }
            }
        }

        int offset = pageNumber * pageSize;
        return entityManager.createQuery(query)
                .setFirstResult(offset)
                .setMaxResults(pageSize)
                .getResultList();
    }

    /**
     * Count users with conditions by Criteria
     *
     * @param criteriaList value of multiple columns
     * @param address default field city in table tbl_address
     * @return
     */
    private Long getTotalElements(List<SearchCriteria> criteriaList, String address) {
        log.info("-------------- getTotalElements --------------");

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<User> userRoot = query.from(User.class);

        Predicate predicate = criteriaBuilder.conjunction();
        UserSearchCriteriaQueryConsumor searchConsumor = new UserSearchCriteriaQueryConsumor(predicate, criteriaBuilder, userRoot);

        if (StringUtils.hasLength(address)) {
            Join<Address, User> userAddressJoin = userRoot.join("addresses");
            Predicate addressPredicate = criteriaBuilder.like(criteriaBuilder.lower(userAddressJoin.get("city")),
                    String.format(LIKE_FORMAT, address.toLowerCase()));
            criteriaList.forEach(searchConsumor);
            predicate = searchConsumor.getPredicate();
            query.select(criteriaBuilder.count(userRoot));
            query.where(predicate, addressPredicate);
        } else {
            criteriaList.forEach(searchConsumor);
            predicate = searchConsumor.getPredicate();
            query.select(criteriaBuilder.count(userRoot));
            query.where(predicate);
        }

        return entityManager.createQuery(query).getSingleResult();
    }

    public PageResponse<?> getUsersJoinedAdress(Pageable pageable, String[] user, String[] address) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);
        Root<User> userRoot = query.from(User.class);
        Join<Address, User> addressUserJoin = userRoot.join("addresses");

        // build query
        List<Predicate> userPreList = new ArrayList<>();
        Pattern pattern = Pattern.compile(AppConst.SEARCH_SPEC_OPERATOR);
        for (String u : user) {
            Matcher matcher = pattern.matcher(u);
            if (matcher.find()) {
                SpecSearchCriteria criteria = new SpecSearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                Predicate predicate = toUserPredicate(userRoot, criteriaBuilder, criteria);
                userPreList.add(predicate);
            }
        }

        List<Predicate> addressPreList = new ArrayList<>();
        for (String a : address) {
            Matcher matcher = pattern.matcher(a);
            if (matcher.find()) {
                SpecSearchCriteria criteria = new SpecSearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                Predicate predicate = toAddressPredicate(addressUserJoin, criteriaBuilder, criteria);
                addressPreList.add(predicate);
            }
        }

        Predicate userPre = criteriaBuilder.or(userPreList.toArray(new Predicate[0]));
        Predicate addressPre = criteriaBuilder.or(addressPreList.toArray(new Predicate[0]));
        Predicate finalPre = criteriaBuilder.and(userPre, addressPre); // các điều kiện của user liên kết với các điều kiện của address thông qua toán tử and

        query.where(finalPre);

        List<User> users = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        long count = countUserJoinAddress(user, address);

        return PageResponse.builder()
                .pageNo(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPages(count)
                .items(users)
                .build();
    }

    private Long countUserJoinAddress(String[] user, String[] address) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<User> userRoot = query.from(User.class);
        Join<Address, User> addressUserJoin = userRoot.join("addresses");

        // build query
        List<Predicate> userPreList = new ArrayList<>();
        Pattern pattern = Pattern.compile("(\\w+?)([<:>~!])(.*)(\\p{Punct}?)(\\p{Punct}?)");
        for (String u : user) {
            Matcher matcher = pattern.matcher(u);
            if (matcher.find()) {
                SpecSearchCriteria criteria = new SpecSearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                Predicate predicate = toUserPredicate(userRoot, criteriaBuilder, criteria);
                userPreList.add(predicate);
            }
        }

        List<Predicate> addressPreList = new ArrayList<>();
        for (String a : address) {
            Matcher matcher = pattern.matcher(a);
            if (matcher.find()) {
                SpecSearchCriteria criteria = new SpecSearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                Predicate predicate = toAddressPredicate(addressUserJoin, criteriaBuilder, criteria);
                addressPreList.add(predicate);
            }
        }

        Predicate userPre = criteriaBuilder.or(userPreList.toArray(new Predicate[0]));
        Predicate addressPre = criteriaBuilder.or(addressPreList.toArray(new Predicate[0]));
        Predicate finalPre = criteriaBuilder.and(userPre, addressPre);

        query.select(criteriaBuilder.count(userRoot));
        query.where(finalPre);

        return entityManager.createQuery(query).getSingleResult();

    }

    public Predicate toUserPredicate(@NonNull final Root<User> root, @NonNull final CriteriaBuilder criteriaBuilder, SpecSearchCriteria criteria) {
        return switch (criteria.getOperation()) {
            case EQUALITY -> criteriaBuilder.equal(root.get(criteria.getKey()), criteria.getValue());
            case NEGATION -> criteriaBuilder.notEqual(root.get(criteria.getKey()), criteria.getValue());
            case GREATER_THAN -> criteriaBuilder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN -> criteriaBuilder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LIKE -> criteriaBuilder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString() + "%");
            case STARTS_WITH -> criteriaBuilder.like(root.get(criteria.getKey()), criteria.getValue().toString() + "%");
            case ENDS_WITH -> criteriaBuilder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString());
            case CONTAINS -> criteriaBuilder.like(root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
        };
    }

    public Predicate toAddressPredicate(@NonNull final Join<Address, User> root, @NonNull final CriteriaBuilder criteriaBuilder, SpecSearchCriteria criteria) {
        return switch (criteria.getOperation()) {
            case EQUALITY -> criteriaBuilder.equal(root.get(criteria.getKey()), criteria.getValue());
            case NEGATION -> criteriaBuilder.notEqual(root.get(criteria.getKey()), criteria.getValue());
            case GREATER_THAN -> criteriaBuilder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN -> criteriaBuilder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LIKE -> criteriaBuilder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString() + "%");
            case STARTS_WITH -> criteriaBuilder.like(root.get(criteria.getKey()), criteria.getValue().toString() + "%");
            case ENDS_WITH -> criteriaBuilder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString());
            case CONTAINS -> criteriaBuilder.like(root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
        };
    }
}
