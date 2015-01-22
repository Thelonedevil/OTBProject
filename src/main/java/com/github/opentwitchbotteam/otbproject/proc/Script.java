package com.github.opentwitchbotteam.otbproject.proc;

import com.github.opentwitchbotteam.otbproject.database.DatabaseWrapper;
import com.github.opentwitchbotteam.otbproject.users.UserLevel;

public interface Script {
    void excecute(DatabaseWrapper db, String[] commandArgs, String execChannel, String targetChannel, String user, UserLevel userLevel);
}
