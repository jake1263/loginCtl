package com.gitee.taven.filter;

import com.gitee.taven.pojo.UserBO;
import com.gitee.taven.utils.JWTUtil;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 比较时间戳
 */
public class CompareKickOutFilter extends KickOutFilter {
	@Override
	public boolean isAccessAllowed(HttpServletRequest request, HttpServletResponse response) {
		String token = request.getHeader("Authorization");
		String username = JWTUtil.getUsername(token);
		String userKey = PREFIX + username;

		RBucket<String> bucket = redissonClient.getBucket(userKey);
		String redisToken = bucket.get();

		if (StringUtils.isBlank(redisToken)) {// 第一次设置
			bucket.set(token);
		} else if (token.equals(redisToken)) { // 相同的token
			return true;
		} else {
			Long redisTokenUnixTime = JWTUtil.getClaim(redisToken, "createTime").asLong();
			Long tokenUnixTime = JWTUtil.getClaim(token, "createTime").asLong();
			if (tokenUnixTime.compareTo(redisTokenUnixTime) > 0) {
				// 传进来的token是离现在最新的，覆盖旧的token
				bucket.set(token);
			} else {
				// 注销当前token
				userService.logout(token);
				sendJsonResponse(response, 4001, "您的账号已在其他设备登录");
				return false;
			}
		}
		return true;
	}
}
