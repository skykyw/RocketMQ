/**
 * Copyright (C) 2010-2013 Alibaba Group Holding Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.rocketmq.tools.command.namesrv;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import com.alibaba.rocketmq.common.UtilAll;
import com.alibaba.rocketmq.srvutil.ServerUtil;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.alibaba.rocketmq.tools.command.SubCommand;


/**
 * 取得 project group 配置信息
 * 
 * @author: manhong.yqd<jodie.yqd@gmail.com>
 * @since: 13-8-29
 */
public class GetProjectGroupCommand implements SubCommand {
    @Override
    public String commandName() {
        return "getProjectGroup";
    }


    @Override
    public String commandDesc() {
        return "get project group by server ip or project group name.";
    }


    @Override
    public Options buildCommandlineOptions(Options options) {
        Option opt = new Option("i", "ip", true, "set the server ip");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("p", "project", true, "set the project group");
        opt.setRequired(false);
        options.addOption(opt);
        return options;
    }


    @Override
    public void execute(CommandLine commandLine, Options options) {
        DefaultMQAdminExt defaultMQAdminExt = new DefaultMQAdminExt();
        defaultMQAdminExt.setInstanceName(Long.toString(System.currentTimeMillis()));
        try {
            if (commandLine.hasOption("i")) {
                String ip = commandLine.getOptionValue('i').trim();
                defaultMQAdminExt.start();
                String project = defaultMQAdminExt.getProjectGroupByIp(ip);
                System.out.printf("ip=%s, projectGroup=%s\n", ip, project);
            }
            else if (commandLine.hasOption("p")) {
                String project = commandLine.getOptionValue('p').trim();
                defaultMQAdminExt.start();
                String ips = defaultMQAdminExt.getIpsByProjectGroup(project);
                if (UtilAll.isBlank(ips)) {
                    System.out.printf("No ip in project group[%s]\n", project);
                }
                else {
                    System.out.printf("projectGroup=%s, ips=%s\n", project, ips);
                }
            }
            else {
                ServerUtil.printCommandLineHelp("mqadmin " + this.commandName(), options);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            defaultMQAdminExt.shutdown();
        }
    }
}
