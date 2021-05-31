package cn.coonu.common.utils.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log<T> {

    private Logger log;

    public Log(Class<T> c) {
        log = LoggerFactory.getLogger(c);
    }

    public void debug(String msg, Object... objects) {  //这里其实就只是减少了代码, 后面isDebugEnabled的作用根本没有起到, 其作用是如果debug未启用, 那么不运行debug参数中是表达式的部分, 所以这个类作用不大, 只是减少了代码而已;
        if (log.isDebugEnabled()) {
            log.debug(msg, objects);
        }
    }

}

