package jqyzyh.iee.commen;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jqyzyh on 2016/7/22.
 * 基础工具
 */
public class CommonUtils {
    /**
     * 用于校验手机号码的正则表达式
     */
    Pattern PATTERN_MOBILE = Pattern.compile("^(0|(\\+)?86|17951)?(13[0-9]|15[012356789]|17[0678]|18[0-9]|14[57])[0-9]{8}$");

    /**
     * 校验是否为手机号
     * @param mobileNo 被校验字符串
     * @return true是手机号，否则返回false
     */
    public boolean checkMobileNo(String mobileNo){
        Matcher matcher = PATTERN_MOBILE.matcher(mobileNo);
        return matcher.matches();
    }
}
