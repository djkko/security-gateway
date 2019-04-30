package cn.denvie.api.gateway.repository;

import cn.denvie.api.gateway.core.ApiToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * ApiToken仓库。
 *
 * @author DengZhaoyong
 * @version 1.1.0
 */
@Repository
public interface ApiTokenRepository extends JpaRepository<ApiToken, Long> {

    /**
     * 根据用户Id查找所有的ApiToken。
     *
     * @param userId 用户Id
     * @return
     */
    List<ApiToken> findAllByUserId(String userId);

    /**
     * 根据用户Id、设备类型、设备标识查找ApiToken。
     *
     * @param userId     用户Id
     * @param clientType 客户端类别，android、ios、web...
     * @param clientCode 设备标识
     * @return ApiToken
     */
    ApiToken findByUserIdAndClientTypeAndClientCode(String userId, String clientType, String clientCode);

    /**
     * 根据token值查找ApiToken。
     *
     * @param accessToken token值
     * @return ApiToken
     */
    ApiToken findByAccessToken(String accessToken);

    /**
     * 根据用户Id删除ApiToken。
     *
     * @param userId     用户Id
     * @return
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM ApiToken AT WHERE AT.userId = :userId")
    int deleteByUserIdEquals(@Param("userId") String userId);

}
