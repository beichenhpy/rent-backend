package com.hpy.RentHouse.admin.dao;

import DO.CityDo;
import DO.ProvinceDo;
import DO.VillageDo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/4/10 17:46
 */
@Repository
public interface AreaMapper {

    //查询 所有省
    @Select("select * from province")
    List<ProvinceDo> findAllProvince();

    //查询省和其对应的城市
    @Select("select * from province")
    @Results(id = "provinceMap", value = {
            @Result(id = true, property = "pid", column = "pid"),
            @Result(property = "name", column = "name"),
            @Result(property = "children", column = "pid", many = @Many(select = "findCitiesByPid"))
    })
    List<ProvinceDo> findAllProvinceAndCities();

    //根据省查询城市
    @Select("select * from city where pid = #{pid}")
    @Results(id = "cityMap",value = {
            @Result(id = true,property = "cid",column = "cid"),
            @Result(property = "name",column = "name"),
            @Result(property = "pid",column = "pid"),
            @Result(property = "children",column = "cid",many = @Many(select = "findVillagesByCid"))
    })
    List<CityDo> findCitiesByPid(String pid);

    //根据城市查询区县
    @Select("select * from village where cid = #{cid}")
    List<VillageDo> findVillagesByCid(String cid);

    //添加省信息
    @Insert("insert into province (pid, name) VALUES (#{pid},#{name})")
    void addProvince(ProvinceDo provinceDo);

    //删除省
    @Delete("delete from province where pid = #{pid}")
    Integer deleteProvince(String pid);

    //更新
    @Update("update province set name = #{name} where pid =#{pid}")
    void updateProvince(ProvinceDo provinceDo);

    //添加城市
    @Insert("insert into city (name, pid,cid) values (#{name},#{pid},#{cid})")
    void addCity(CityDo cityDo);

    //删除城市
    @Delete("delete from city where cid = #{cid}")
    Integer deleteCity(String cid);

    //更新城市名
    @Update("update city set name = #{name} where cid = #{cid}")
    void updateCity(CityDo cityDo);

    //添加区名
    @Insert("insert into village (vid, name, cid) VALUES (#{vid},#{name},#{cid})")
    void addVillage(VillageDo villageDo);

    //删除区
    @Delete("delete from village where vid = #{vid}")
    Integer deleteVillage(String vid);

    //更新区名
    @Update("update village set name = #{name} where vid = #{vid}")
    void updateVillage(VillageDo villageDo);
}
