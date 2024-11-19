package com.lingoutil.workcopilot.handler;

import com.lingoutil.workcopilot.config.YamlConfig;
import com.lingoutil.workcopilot.util.LogUtil;

import java.util.List;

import static com.lingoutil.workcopilot.constant.Constant.*;

public class RenameCommandHandler extends CommandHandler {
    @Override
    protected List<String> loadCommandList() {
        return renameCommands;
    }

    @Override
    protected void process(String[] argv) {
        String script = argv[0];
        String alias = argv[2];
        String newAlias = argv[3];

        String path = YamlConfig.getProperty(PATH, alias);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(script)
                .append(" ")
                .append(removeCommands.get(0))
                .append(" ")
                .append(alias);

        String[] removeArgv = stringBuilder.toString().split(" ");
        CommandHandler.execute(removeArgv);

        stringBuilder.setLength(0);
        stringBuilder.append(script)
                .append(" ")
                .append(addCommands.get(0))
                .append(" ")
                .append(newAlias)
                .append(" ")
                .append(path);
        String[] addArgv = stringBuilder.toString().split(" ");
        CommandHandler.execute(addArgv);

        LogUtil.info("Rename %s to %s successfully", alias, newAlias);
    }

    @Override
    protected boolean checkArgs(String[] argv) {
        return checkArgs(argv, 4, this::hint);
    }

    @Override
    protected void hint(String[] argv) {
        LogUtil.usage("%s %s <alias> <new_alias>", argv[0], argv[1]);
    }
}
