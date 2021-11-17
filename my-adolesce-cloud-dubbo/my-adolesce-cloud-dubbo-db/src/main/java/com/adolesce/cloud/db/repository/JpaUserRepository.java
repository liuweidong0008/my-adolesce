package com.adolesce.cloud.db.repository;

import com.adolesce.cloud.dubbo.domain.db.JpaUser;
import com.adolesce.cloud.dubbo.domain.db.JpaUserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface JpaUserRepository extends JpaRepository<JpaUser, Long> {
    /**
     * 自定义简单查询
     */
    JpaUser findByUserName(String paramString);

    JpaUser findByUserNameAndAge(String paramString, int paramInt);

    JpaUser findByUserNameAndAgeAndPassword(String paramString1, int paramInt, String paramString2);

    List<JpaUser> findByUserNameOrUserNameAndAge(String paramString1, String paramString2, int paramInt);

    long countByUserName(String paramString);

    List<JpaUser> findByUserNameLike(String paramString);

    List<JpaUser> findByUserNameIgnoreCase(String paramString);

    List<JpaUser> findByUserNameIgnoreCaseOrPasswordLikeOrderByAgeDesc(String userName, String password);

    List<JpaUser> findByBirthdayIs(Date birthday);

    List<JpaUser> findByBirthdayBetween(Date startTime, Date endTime);

    List<JpaUser> findByCreateTimeBetween(Date startTime, Date endTime);

    List<JpaUser> findByCreateTimeGreaterThanEqualAndCreateTimeLessThanEqual(Date startTime, Date endTime);

    Page<JpaUser> findByUserName(String paramString, Pageable pageable);

    JpaUser findFirstByOrderByAgeAsc();

    List<JpaUser> findTop3ByUserName(String zhaoyun, Pageable pageable);

    Page<JpaUser> findFirst3ByUserName(Pageable pageable, String zhaoyun);

    /**
     * JPQL方式查询
     */
    @Transactional(readOnly = true)
    @Query("select u from my_jpa_user u where u.password = ?1")
    List<JpaUser> myFindByPassword(String password);

    @Query("select u from my_jpa_user u where u.userName = ?1")
    List<JpaUser> myFindByUserNameWithOrder(String zhaoyun, Sort sort);

    @Query("select u from my_jpa_user u where u.userName = ?1")
    Page<JpaUser> myPageByUserName(Pageable pageable, String zhaoyun);

    @Modifying
    @Transactional // springdata jpa使用jpql执行插入,更新,删除需要手动提交事务
    @Query("update my_jpa_user u set u.age = ?1 where u.id= ?2")
    void myUpdateAgeById(int paramInt, long paramLong);

    /**
     * SQL方式查询
     * nativeQuery:默认的是false.表示不开启sql查询。是否对value中的语句做转义。
     */
    @Query(value = "select * from my_jpa_user where user_name = ?", nativeQuery = true)
    List<JpaUser> myFindByUserName(String userName);

    @Query(value = "select * from my_jpa_user where user_name like ?", nativeQuery = true)
    List<JpaUser> myFindByLikeUserName(String userName);

    @Query(value = "select * from my_jpa_user where user_name = ?1 and age >= ?2", nativeQuery = true)
    List<JpaUser> myFindByUserNameAndgAge(String userName, Integer age);

    @Query(value = "select * from my_jpa_user where user_name = ?", nativeQuery = true)
    Page<JpaUser> myFindByUserNameWithPage(String nickName, Pageable pageable);

    @Query(value = "select u.id,u.user_name as userName,u.password,u.name,u.age,u.sex,u.phone,u.birthday,i.card_no as cardNo,i.start_time as startTime,i.end_time as endTime " +
            "from my_jpa_user u inner join my_jpa_identity i on i.userid = u.id where u.id = ?1",nativeQuery = true)
    JpaUserInfo myFindUserInfoByUserId(long userId);
}