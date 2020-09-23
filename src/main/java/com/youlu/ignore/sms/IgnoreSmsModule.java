package com.youlu.ignore.sms;

import com.alibaba.jvm.sandbox.api.Information;
import com.alibaba.jvm.sandbox.api.Module;
import com.alibaba.jvm.sandbox.api.annotation.Command;
import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.AdviceListener;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import com.niceloo.core.bean.CoreApiContext;
import org.apache.catalina.connector.RequestFacade;
import org.kohsuke.MetaInfServices;
import org.nobject.common.lang.ThreadUtils;

import javax.annotation.Resource;

/**
 * @author zhushilong
 */
@MetaInfServices(Module.class)
@Information(id = "dynamicParam",author = "一朵彼岸花",version = "0.0.1")
public class IgnoreSmsModule implements Module {

    @Resource
    private ModuleEventWatcher moduleEventWatcher;

    @Command("env")
    public void ignoreSms() {

        new EventWatchBuilder(moduleEventWatcher)
//                .onClass("com.niceloo.mc.service.api.impl.*")
                .onClass("org.nobject.common.db.DBFactory")
                .onBehavior("parse")
//                .onBehavior("send")
                .onWatch(new AdviceListener() {

                    @Override
                    protected void afterReturning(Advice advice) {
                        System.out.println("进来了" );
                        System.out.println("advice = " + advice.isProcessTop());
                        String env = null;
                        try {
                            CoreApiContext apiContext = (CoreApiContext) ThreadUtils.get("_APICONTEXT_");
                            org.apache.catalina.connector.RequestFacade request = (RequestFacade) apiContext.getEnv().get("request");
                             env = request.getHeader("env");
                            System.out.println("env = " + env);
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
//                      // 修改方法返回值 获取方法返回值
                        Object returnObj = advice.getReturnObj();
                        Object[] objects = (Object[]) returnObj;
                        //解析SQL强大工具类
                        //获取SQL
                        String oldSQL = objects[0].toString();
                        oldSQL = oldSQL.replaceAll("UcUserbrand","UcUserbrand_"+ env);
                        oldSQL = oldSQL.replaceAll("UcBrand","UcBrand_"+ env);

                        objects[0] = oldSQL;
                        System.out.println("SQL = " + objects[0]);
                    }
                });

    }

}
