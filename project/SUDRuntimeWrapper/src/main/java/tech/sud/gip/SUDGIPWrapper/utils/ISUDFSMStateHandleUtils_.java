/*
 * Copyright © Sud.Tech
 * https://sud.tech
 */

package tech.sud.gip.SUDGIPWrapper.utils;

import tech.sud.gip.SUDGIPWrapper.state.MGStateResponse;
import tech.sud.gip.core.ISUDFSMStateHandle;

public class ISUDFSMStateHandleUtils_ {

    /**
     * 回调游戏，成功
     *
     * @param handle
     */
    public static void handleSuccess(ISUDFSMStateHandle handle) {
        MGStateResponse response = new MGStateResponse();
        response.ret_code = MGStateResponse.SUCCESS;
        response.ret_msg = "success";
        handle.success(SUDJsonUtils_.toJson(response));
    }

}
