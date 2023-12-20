package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);
    @Select("select * from employee where id = #{id}")
    Employee getById(Long id);

    @Insert("insert into employee " +
            "(name,username,password,phone,sex,id_number,create_time,update_time,create_user,update_user,status)" +
            "VALUES "+
            "(#{name},#{username},#{password},#{phone},#{sex},#{idNumber},#{createTime},#{updateTime},#{createUser},#{updateUser},#{status})")
    void insert(Employee employee);

    //动态sql，写在配置文件中
    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);
    //动态sql，写在配置文件中
    void update(Employee employee);
}
