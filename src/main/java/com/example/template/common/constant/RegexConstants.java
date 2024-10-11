package com.example.template.common.constant;

/**
 * 常用正则表达式常量
 *
 * @author hzh
 * @data 2024/10/7 15:50
 */
public interface RegexConstants {
    /**
     * 账号只能包含字母、数字和下划线，且不能以数字开头
     */
    String VALID_USER_ACCOUNT_REGEX = "^[a-zA-Z][a-zA-Z0-9_]*$";
    /**
     * 密码至少 8 个字符，至少包含 1 个大写字母、1 个小写字母、1 个数字和 1 个特殊字符
     */
    String VALID_USER_PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-\\[\\]{};':\",.<>?/`~]).{8,}$";

    /**
     * 只包含数字和英文的正则表达式
     */
    String REGEX_NUMBER_AND_LETTER = "^[0-9a-zA-Z]+$";

    /**
     * 邮箱的正则表达式
     */
    String REGEX_MAIL = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
}
