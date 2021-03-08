package com.kan.codingchallengesfossil3.common

/**
 * Created by Kan on 3/8/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */

sealed class Failure {

    /* object NetworkConnection : Failure()
     class ServerError(val message: String = ResourceUtil.getString(R.string.server_error_default)) :
         Failure()

     object AuthenticateFail : Failure()
     object AuthenticateRefreshToken : Failure()
     object PermissionDenied : Failure()
     class CanNotContinue(val error: List<ResponseEntity.ErrorEntity>) : Failure()
     class NotFoundError(val error: List<ResponseEntity.ErrorEntity>) : Failure()
     class BadRequest(val error: List<ResponseEntity.ErrorEntity>) : Failure()*/

    /** * Extend this class for feature specific failures.*/
    abstract class FeatureFailure : Failure()
}