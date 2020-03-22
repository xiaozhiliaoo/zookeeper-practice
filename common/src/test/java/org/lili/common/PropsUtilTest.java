package org.lili.common;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author lili
 * @date 2020/3/22 19:01
 * @description
 */
public class PropsUtilTest {

    @Test
    public void getString() {
        String config = PropsUtil.getString("zk-config");
        System.out.println(config);
    }
}