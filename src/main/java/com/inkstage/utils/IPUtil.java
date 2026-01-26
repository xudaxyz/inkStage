package com.inkstage.utils;


import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * IP工具类, 用于获取客户端真实IP地址
 */
public class IPUtil {

    /**
     * 获取客户端真实IP地址
     * 处理各种代理服务器的IP转发, 获取真实客户端IP
     *
     * @return 客户端真实IP地址
     */
    public static String getClientIp() {
        // 获取当前请求上下文
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return "127.0.0.1"; // 默认本地IP
        }
        HttpServletRequest request = attributes.getRequest();

        // 依次检查各种代理IP头, 获取真实客户端IP
        String ip = getIpFromHeader(request, "X-Forwarded-For");
        if (isValidIp(ip)) {
            return ip;
        }

        ip = getIpFromHeader(request, "Proxy-Client-IP");
        if (isValidIp(ip)) {
            return ip;
        }

        ip = getIpFromHeader(request, "WL-Proxy-Client-IP");
        if (isValidIp(ip)) {
            return ip;
        }

        ip = getIpFromHeader(request, "HTTP_CLIENT_IP");
        if (isValidIp(ip)) {
            return ip;
        }

        ip = getIpFromHeader(request, "HTTP_X_FORWARDED_FOR");
        if (isValidIp(ip)) {
            return ip;
        }

        // 最后使用request.getRemoteAddr()获取IP
        ip = request.getRemoteAddr();

        // 处理IPv6本地地址
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }

        return ip;
    }

    /**
     * 从请求头中获取IP地址
     *
     * @param request    请求对象
     * @param headerName 头名称
     * @return IP地址或null
     */
    private static String getIpFromHeader(HttpServletRequest request, String headerName) {
        String ip = request.getHeader(headerName);
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            return null;
        }

        // 处理多个IP地址, 取第一个 (真实客户端IP)
        if (ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip.trim();
    }

    /**
     * 验证IP地址是否有效
     *
     * @param ip IP地址
     * @return 是否有效
     */
    private static boolean isValidIp(String ip) {
        return ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip);
    }

}
