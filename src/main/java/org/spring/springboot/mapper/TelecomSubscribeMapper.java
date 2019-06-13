package org.spring.springboot.mapper;

import org.apache.ibatis.annotations.Param;
import org.spring.springboot.domain.TelecomSubscribe;
import org.springframework.stereotype.Repository;


@Repository
public interface TelecomSubscribeMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TelecomSubscribe record);

    int insertSelective(TelecomSubscribe record);

    TelecomSubscribe selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TelecomSubscribe record);

    int updateByPrimaryKey(TelecomSubscribe record);

    int selectByNotifyType(@Param("notifyType") String notifyType);

}