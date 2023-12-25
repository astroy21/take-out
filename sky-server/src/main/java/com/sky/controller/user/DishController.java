package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "C端-菜品浏览接口")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据分类id查询菜品
     * 后期修改为结合redis
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> list(Long categoryId) {
        //1. 构造key
        String key = "dish_"+categoryId;
        //2. redis中查询对应的key
        List<DishVO> dishVOList = (List<DishVO>) redisTemplate.opsForValue().get(key);
        //3. 如果redis已有值，直接返回
        if(dishVOList!=null && !dishVOList.isEmpty()){
            return Result.success(dishVOList);
        }
        //4. redis没有对应key，需查询sql并放入redis
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品

        dishVOList = dishService.listWithFlavor(dish);
        redisTemplate.opsForValue().set(key,dishVOList);

        return Result.success(dishVOList);
    }

}
