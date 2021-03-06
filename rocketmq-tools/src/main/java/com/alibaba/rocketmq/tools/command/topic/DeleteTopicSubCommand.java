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
package com.alibaba.rocketmq.tools.command.topic;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import com.alibaba.rocketmq.srvutil.ServerUtil;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.alibaba.rocketmq.tools.command.CommandUtil;
import com.alibaba.rocketmq.tools.command.SubCommand;


/**
 * 删除Topic配置命令
 * 
 * @author manhong.yqd<manhong.yqd@alibaba-inc.com>
 * @since 2013-8-21
 */
public class DeleteTopicSubCommand implements SubCommand {
    @Override
    public String commandName() {
        return "deleteTopic";
    }


    @Override
    public String commandDesc() {
        return "delete topic from broker and NameServer.";
    }


    @Override
    public Options buildCommandlineOptions(Options options) {
        Option opt = new Option("t", "topic", true, "topic name");
        opt.setRequired(true);
        options.addOption(opt);

        opt = new Option("c", "clusterName", true, "delete topic from which cluster");
        opt.setRequired(true);
        options.addOption(opt);

        return options;
    }


    @Override
    public void execute(CommandLine commandLine, Options options) {
        DefaultMQAdminExt adminExt = new DefaultMQAdminExt();
        adminExt.setInstanceName(Long.toString(System.currentTimeMillis()));
        try {
            String topic = commandLine.getOptionValue('t').trim();

            if (commandLine.hasOption('c')) {
                String clusterName = commandLine.getOptionValue('c').trim();

                adminExt.start();
                // 删除 broker 上的 topic 信息
                Set<String> masterSet = CommandUtil.fetchMasterAddrByClusterName(adminExt, clusterName);
                adminExt.deleteTopicInBroker(masterSet, topic);
                System.out.printf("delete topic [%s] from cluster [%s] success.\n", topic, clusterName);

                // 删除 NameServer 上的 topic 信息
                Set<String> nameServerSet = null;
                if (commandLine.hasOption('n')) {
                    String[] ns = commandLine.getOptionValue('n').trim().split(";");
                    nameServerSet = new HashSet(Arrays.asList(ns));
                }
                adminExt.deleteTopicInNameServer(nameServerSet, topic);
                System.out.printf("delete topic [%s] from NameServer success.\n", topic);
                return;
            }

            ServerUtil.printCommandLineHelp("mqadmin " + this.commandName(), options);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            adminExt.shutdown();
        }
    }
}
