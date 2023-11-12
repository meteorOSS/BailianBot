package com.meteor.bailianbot;

import com.aliyun.broadscope.bailian.sdk.models.CompletionsResponse;

public interface CallBack {
    void call(CompletionsResponse completionsResponse);
}
