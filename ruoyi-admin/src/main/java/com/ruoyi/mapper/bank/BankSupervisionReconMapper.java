package com.ruoyi.mapper.bank;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.ruoyi.domain.bank.BankSupervisionReconRun;

public interface BankSupervisionReconMapper
{
    @Insert("INSERT INTO bank_supervision_recon_run (institution_id, account_no, account_name, trandt, status, file_name, retry_count, create_by, create_time) VALUES (#{institutionId}, #{accountNo}, #{accountName}, #{trandt}, #{status}, #{fileName}, #{retryCount}, #{createBy}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "runId")
    int insertRun(BankSupervisionReconRun run);

    @Update("UPDATE bank_supervision_recon_run SET status=#{status}, file_name=#{fileName}, file_size=#{fileSize}, file_md5=#{fileMd5}, saved_path=#{savedPath}, resp_code=#{respCode}, resp_msg=#{respMsg}, total_rows=#{totalRows}, matched_rows=#{matchedRows}, diff_rows=#{diffRows}, retry_count=#{retryCount}, update_time=NOW() WHERE run_id=#{runId}")
    int updateRun(BankSupervisionReconRun run);

    @Select("SELECT * FROM bank_supervision_recon_run WHERE run_id=#{id}")
    @Results(id = "BankSupervisionReconRunResult", value = {
            @Result(property = "runId", column = "run_id"),
            @Result(property = "institutionId", column = "institution_id"),
            @Result(property = "accountNo", column = "account_no"),
            @Result(property = "accountName", column = "account_name"),
            @Result(property = "trandt", column = "trandt"),
            @Result(property = "status", column = "status"),
            @Result(property = "fileName", column = "file_name"),
            @Result(property = "fileSize", column = "file_size"),
            @Result(property = "fileMd5", column = "file_md5"),
            @Result(property = "savedPath", column = "saved_path"),
            @Result(property = "respCode", column = "resp_code"),
            @Result(property = "respMsg", column = "resp_msg"),
            @Result(property = "totalRows", column = "total_rows"),
            @Result(property = "matchedRows", column = "matched_rows"),
            @Result(property = "diffRows", column = "diff_rows"),
            @Result(property = "retryCount", column = "retry_count"),
            @Result(property = "createBy", column = "create_by"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time")
    })
    BankSupervisionReconRun selectRun(@Param("id") Long id);

    /** institutionIds 为 null 表示不限机构（管理员/监管员）；空列表表示无可见机构。 */
    @Select({ "<script>",
            "SELECT * FROM bank_supervision_recon_run",
            "<if test='institutionIds != null'>",
            "  <choose>",
            "    <when test='institutionIds.size() > 0'> WHERE institution_id IN ",
            "      <foreach collection='institutionIds' item='institutionId' open='(' separator=',' close=')'>#{institutionId}</foreach>",
            "    </when>",
            "    <otherwise> WHERE 1=0</otherwise>",
            "  </choose>",
            "</if>",
            " ORDER BY run_id DESC LIMIT #{limit}",
            "</script>" })
    @ResultMap("com.ruoyi.mapper.bank.BankSupervisionReconMapper.BankSupervisionReconRunResult")
    List<BankSupervisionReconRun> selectRuns(@Param("institutionIds") List<Long> institutionIds,
            @Param("limit") int limit);
}
