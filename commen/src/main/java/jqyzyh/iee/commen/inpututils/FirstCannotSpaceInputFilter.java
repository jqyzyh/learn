package jqyzyh.iee.commen.inpututils;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

/**
 * 第一个不能输入空格
 * @author jqyzyh
 */
public class FirstCannotSpaceInputFilter implements InputFilter {

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if(dstart == 0 && start == 0){/*判断是否为首字符*/
            if(!TextUtils.isEmpty(source)){/*判断输入内容是否为空*/
                int firstNotSpace = 0;/*第一个不是空格字符的index*/
                for(int i = start ; i < end; i ++){/*循环输入内容*/
                    char c = source.charAt(i);
                    if(Character.isHighSurrogate(c) || c != ' '){/*如果不是空格跳出循环并将index赋值给firstNotSpace*/
                        firstNotSpace = i;
                        break;
                    }
                }
                if(firstNotSpace > 0){/*如果开头有空格字符 截取字符串返回*/
                    return firstNotSpace < source.length()? source.subSequence(0, firstNotSpace) : "";
                }
            }
        }
        return null;
    }
}
