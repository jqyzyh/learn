package jqyzyh.iee.cusomwidget.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author yuhang
 */

public class MethodUtils {

    public static boolean invoke(Object target, String name, Object[] objects){
        if(target == null){
            return false;
        }

        Class[] paramsTypes = null;
        if(objects != null){
            paramsTypes = new Class[objects.length];
            for(int i = 0; i < objects.length; i ++){
                paramsTypes[i] = objects[i].getClass();
            }
        }

        Method method = getMethod(target.getClass(), name, paramsTypes);

        if(method != null){
            try {
                method.invoke(target, objects);
                return true;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Method getMethod(Class cls, String name, Class[] paramTypes){
        if(cls == null){
            return null;
        }

        try {
            Method method = cls.getDeclaredMethod(name, paramTypes);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
        }

        if(cls.getSuperclass() != null){
            return getMethod(cls.getSuperclass(), name, paramTypes);
        }
        return null;
    }
}
