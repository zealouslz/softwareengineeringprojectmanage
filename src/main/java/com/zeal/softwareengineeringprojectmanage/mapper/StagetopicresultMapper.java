package com.zeal.softwareengineeringprojectmanage.mapper;

import com.zeal.softwareengineeringprojectmanage.bean.Stagetopicresult;
import com.zeal.softwareengineeringprojectmanage.bean.StagetopicresultExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface StagetopicresultMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table stagetopicresult
     *
     * @mbggenerated
     */
    int countByExample(StagetopicresultExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table stagetopicresult
     *
     * @mbggenerated
     */
    int deleteByExample(StagetopicresultExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table stagetopicresult
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table stagetopicresult
     *
     * @mbggenerated
     */
    int insert(Stagetopicresult record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table stagetopicresult
     *
     * @mbggenerated
     */
    int insertSelective(Stagetopicresult record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table stagetopicresult
     *
     * @mbggenerated
     */
    List<Stagetopicresult> selectByExample(StagetopicresultExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table stagetopicresult
     *
     * @mbggenerated
     */
    Stagetopicresult selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table stagetopicresult
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") Stagetopicresult record, @Param("example") StagetopicresultExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table stagetopicresult
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") Stagetopicresult record, @Param("example") StagetopicresultExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table stagetopicresult
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(Stagetopicresult record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table stagetopicresult
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(Stagetopicresult record);

   @Select({"<script>","select * from stagetopicresult where stagetopicId in","<foreach collection=\"stageTopicIds\" item=\"stagetopicId\" index=\"index\" open=\"(\" separator=\",\" close=\")\">","#{stagetopicId}","</foreach>","limit #{start},#{pageSize}","</script>"})
    List<Stagetopicresult> selectByStageTopicIdsAndPage(@Param("stageTopicIds") List<Integer> stageTopicIds,@Param("start") Integer start,@Param("pageSize") Integer pageSize);

    @Update("update stagetopicresult set isPass=#{isPass},suggestion=#{suggestion} where id=#{id}")
    int updateByIdAndIsPassAndSugg(@Param("id") Integer id,@Param("isPass") Byte isPass,@Param("suggestion") String suggestion);
}