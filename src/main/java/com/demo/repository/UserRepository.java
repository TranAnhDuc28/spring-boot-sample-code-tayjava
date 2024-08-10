package com.demo.repository;

import com.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    // -- Single field --
    // @Query(value = "select distinct u from User u where u.email= ?1")
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);


    // join table
//    @Query(value = "select u from User u inner join Address a on u.id = a.user.id where a.city=:city")
//    List<User> getAllUser(String city);

    // -- Distinct --
    // @Query(value = "select distinct u from User u where u.firstName=:firstName and u.lastName=:lastName")
//    List<User> findDistinctByFirstNameAndLastName(String firstName, String lastName);


    // -- OR --
    // @Query(value = "select u from User u where u.firstName=:name or u.lastName=:name")
//    List<User> findByFirstNameOrLastName(String firstName, String lastName);


    // -- Is, Equals --
    // @Query(value = "select u from User u where u.firstName=:name")
//    List<User> findByFirstNameIs(String name);
//    List<User> findByFirstNameEquals(String name);
//    List<User> findByFirstName(String name);


    // -- Beetween --
    // @Query(value = "select u from User u where u.createAt between ?1 and ?2")
//    List<User> findByCreateAtBetween(Date startDate, Date endDate);


    // -- Less Than --
    // @Query(value = "select u from User u where u.age < :age")
//    List<User> findByAgeLessThan(int age);
    // @Query(value = "select u from User u where u.age <= :age")
//    List<User> findByAgeLessThanEqual(int age);


    // -- Greater Than --
    // @Query(value = "select u from User u where u.age > :age")
//    List<User> findByAgeGreaterThan(int age);
    // @Query(value = "select u from User u where u.age >= :age")
//    List<User> findByAgeGreaterThanEqual(int age);


    // -- Before and After --
    // @Query(value = "select u from User u where u.createAt < :date")
//    List<User> findByCreateAtBefore(Date date);

    // @Query(value = "select u from User u where u.createAt > :date")
//    List<User> findByCreateAtAfter(Date date);


    // -- IsNull and Null --
    // @Query(value = "select u from User u where u.age is null")
//    List<User> findByAgeIsNull();
//    List<User> findByAgeNull();


    // -- NotNull and IsNotNull --
    // @Query(value = "select u from User u where u.age is not null")
//    List<User> findByAgeIsNotNull();
//    List<User> findByAgeNotNull();


    // -- Like --
    // @Query(value = "select u from User u where u.lastName like :lastName")
//    List<User> findByLastNameLike(String lastName);

    // -- Not like --
    // @Query(value = "select u from User u where u.lastName not like :lastName")
//    List<User> findByLastNameNotLike(String lastName);


    // -- StartingWith --
    // @Query(value = "select u from User u where u.lastName like :lastName% ")
//    List<User> findByLastNameStartingWith(String lastName);


    // -- EndingWith --
    // @Query(value = "select u from User u where u.lastName like %:lastName")
//    List<User> findByLastNameEndingWith(String lastName);

    // -- Containing --
    // @Query(value = "select u from User u where u.lastName like %:name%")
//    List<User> findByLastNameContaining(String name);


    // -- Not --
    // @Query(value = "select u from User u where u.lastName <> :name")
//    List<User> findByLastNameNot(String name);


    // -- In --
    // @Query(value = "select u from User u where u.age in (18, 25, 30)")
//    List<User> findByAgeIn(Collection<Integer> ages);

    // -- Not in --
    // @Query(value = "select u from User u where u.age not in (18, 25, 30)")
//    List<User> findByAgeNotIn(Collection<Integer> ages);

    // -- True/False --
    // @Query(value = "select u from User u where u.activated = true")
//    List<User> findByActivatedTrue();
    // @Query(value = "select u from User u where u.activated = false")
//    List<User> findByActivatedFalse();


    // IgnoreCase
    // @Query(value = "select u from User u where lower(u.lastName) like lower(:name)")
//    List<User> findByFirstNameIgnoreCase(String name);

    // Order by
//    List<User> findByFirstNameOrderByCreateAtDesc(String name);
//
//    List<User> findByFirstNameAndLastNameAllIgnoreCase(String firstName, String lastName);
}
