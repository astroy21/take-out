package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 插入菜品
     * @param dish
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);
    //动态sql
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);
    @Delete("delete from dish where id = #{id}")
    void deleteById(Long id);
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    void deleteByIds(List<Long> ids);
    // 动态sql，属性值为null时不修改
    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);

    //动态sql
    List<Dish> list(Dish dish);

    @Select("select d.* from dish d join setmeal_dish sd where d.id = sd.dish_id and sd.setmeal_id = #{setmealId}")
    List<Dish> getBySetmealId(Long setmealId);

    Integer countByMap(Map map);
}
