package com.game.repository;

import com.game.entity.ActivityRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description:
 * @Author: Jason
 * @CreateDate: 2018/11/25 19:22
 */
@Repository
public interface ActivityRegisterRepository extends JpaRepository<ActivityRegister, Long>{

    /**
     * 查找结果
     * @param activityCode
     * @param phone
     * @return
     */
    public List<ActivityRegister> findByActivityCodeAndPhoneAndDeleted(@Param("activityCode") String activityCode, @Param("phone") String phone, @Param("deleted") boolean deleted);

    /**
     *
     * @param outId
     * @return
     */
    public ActivityRegister findByOutId(@Param("outId") String outId);
}
