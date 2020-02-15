package com.zeal.softwareengineeringprojectmanage.service.impl;

import com.zeal.softwareengineeringprojectmanage.bean.Department;
import com.zeal.softwareengineeringprojectmanage.mapper.DepartmentMapper;
import com.zeal.softwareengineeringprojectmanage.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class DeptServiceImpl implements DeptService {
    @Autowired
    DepartmentMapper departmentMapper;


    /**
     * //将方法的运行结果进行缓存；以后要相同的数据，直接从缓存中获取，不用调用方法
     * CacheManager管理多个Cache组件的，对缓存的真正CRUD操作在Cache组建中，每一个缓存组件有自己唯一一个名字
     * 几个属性：
     * cacheNames/value：指定缓存组件的名字；
     * key:缓存数据时使用的key；可以用它来指定。默认使用方法参数的值
     *     编写spEL;#id,参数的值  #a0 #p0 #root.args[0]
     * keyGenerator：key的生成器；可以自己指定key的生成器的组件id
     *     key/keyGenerator:二选一使用
     * CacheManager:指定缓存管理器；或者cacheResolver指定缓存解析器
     * condition: 指定符合条件下才缓存
     * unless：否定缓存；当unless指定条件为true；方法的返回值就不会缓存；可以获取到此结果进行判断
     *    unless = "#result==null"
     * sync:是否使用异步模式
     *运行流程：
     * @Cacheable:
     *   1.方法运行前，先去查询Cache(缓存组件），按照CacheNames指定的名字获取
     *     （CacheMangager先获取相应的缓存），第一次获取缓存如果没有Cache组件会自动创建
     *    2.去Cache中查找缓存内容，使用一个key，默认就是方法参数
     *      key是按照某种策略生成的，默认是使用keyGenerator生成的，默认使用SimpleKeyGenerator生成key
     *        SimpleKeyGenerator生成key的默认策略：
     *          如果没有参数；key=new SimpleKey()
     *          如果有一个参数：key=参数的值
     *          如果有多个参数；key=new SimpleKey(params);
     *     3.没有查到缓存就调用目标方法
     *     4.将目标方法返回的结果，放进缓存中
     * @Cacheable标注的方法执行之前先来检查缓存中有没有这个数据，按照参数值作为key去查询
     *   如果没有就将运行结果放进缓存 ;以后再来调用就可以直接使用缓存中的数据；
     * @param id
     * @return
     */
    @Cacheable(cacheNames = "dept",condition = "#id>0")
    @Override
    public Department selectByPrimaryKey(Integer id) {
        Department department = departmentMapper.selectByPrimaryKey(id);
        return department;
    }

    @Override
    public void insert(Department department) {

        departmentMapper.insert(department);
    }

    /**
     * @Cache:既调用方法，又更新缓存数据
     * 修改了数据库的某个数据，同时更新缓存
     * 运行时机
     * 1.先调用目标方法
     * 2.将目标方法结果缓存起来
     *
     * 更新完之后查询的还是更新前的结果
     *   要用key = "#department.id"
     *   使更新和查询在缓存中是一个key值
     *   要使取缓存的key和放缓存的key用的是一个
     * @param department
     */
    @CachePut(cacheNames = "dept",key = "#department.id")
    @Override
    public Department updataDept(Department department) {
        departmentMapper.updateByPrimaryKey(department);
        return department;
    }

    /**
     * @CacheEvict:缓存清除
     * key指定要清除的数据
     * allEntries = true :指定清除这个缓存中所有数据
     * beforeInvocation = false； 缓存的清除是否在方法前执行
     *     默认代表是在方法之后执行
     */
    @CacheEvict(value = "dept",key="#id")
    @Override
    public void deletDept(Integer id) {
        departmentMapper.deleteByPrimaryKey(id);
    }
}
