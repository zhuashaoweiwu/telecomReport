package org.spring.springboot.mapper;


import org.apache.ibatis.annotations.Param;
import org.spring.springboot.domain.LightingVolEleRecord;
import org.springframework.stereotype.Repository;


@Repository
public interface LightingVolEleRecordMapper {

    int addLightingVolEleRecord(@Param("record") LightingVolEleRecord lightingVolEleRecord,@Param("tableName") String tablename);


    String selectTableByName(@Param("tablename") String tablename);

    void   createNewTable(@Param("tablename") String tablename);
}
