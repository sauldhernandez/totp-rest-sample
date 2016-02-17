import java.net.URLEncoder

import com.googleAuthTest.rest.models.{ValidationRequest, RegistrationFinishRequest}
import com.googleAuthTest.rest.routes.ApplicationRoutesConsolidated
import com.synergygb.zordon.core.Boot
import com.warrenstrange.googleauth.GoogleAuthenticator
import spray.http.HttpData
import spray.http.MediaTypes._
import spray.http.StatusCodes._

import Models._
import net.glxn.qrgen.core.image.ImageType
import net.glxn.qrgen.javase.QRCode

import scala.concurrent.duration._
/* Copyright (C) 2016 Synergy-GB LLC.
 * All rights reserved.
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 */
/**
  * Created by saul on 2/16/16.
  */
object ServiceBoot extends App with Boot with ApplicationRoutesConsolidated {

  implicit def dataContext = Context
  import Context.executionContext

  override def handleGetRegisterUsernameStart(username: String)() = {
    val googleAuth = new GoogleAuthenticator()

    //Check if user is already registered
    onSuccess(Context.keyValueStore.read[Registration](username)) { maybeRegistration =>
      if (maybeRegistration.isDefined)
        complete(Conflict, "User is already registered")
      else {
        //Save the registration
        val registration = Registration(username, googleAuth.createCredentials().getKey)
        onSuccess(Context.keyValueStore.write(username, registration, 5 minutes)) { x =>
          val uri = s"otpauth://totp/${URLEncoder.encode(username)}?secret=${registration.sharedSecret}&issuer=authTest"
          val imageFile = QRCode.from(uri).withSize(300, 300).to(ImageType.PNG).file()
          respondWithMediaType(`image/png`) & complete(HttpData(imageFile))
        }
      }
    }
  }

  override def handlePostRegisterUsernameFinish(username: String, request: RegistrationFinishRequest)() = {
    onSuccess(Context.keyValueStore.read[Registration](username)) { maybeRegistration =>
      maybeRegistration.map { registration =>
        if(registration.complete)
          complete(Conflict, "This user is already registered")
        else {
          //Validate the codes
          val googleAuth = new GoogleAuthenticator()
          if(googleAuth.authorize(registration.sharedSecret, request.code))
            onSuccess(Context.keyValueStore.write(username, registration.copy(complete = true), Duration.Inf)) { x =>
              complete("Registration successful")
            }
          else {
            complete(BadRequest, "Invalid code")
          }
        }
      }.getOrElse(complete(NotFound, "No registration for that user"))
    }
  }


  override def handlePostValidate(request: ValidationRequest)() = {
    onSuccess(Context.keyValueStore.read[Registration](request.username)) { maybeRegistration =>
      maybeRegistration.map { registration =>
        if(!registration.complete)
          complete(NotFound, "No such user")
        else {
          val googleAuth = new GoogleAuthenticator()
          if(googleAuth.authorize(registration.sharedSecret, request.code))
            complete(OK, "Code is valid")
          else
            complete(BadRequest, "Code is invalid")
        }
      }.getOrElse(complete(NotFound, "No such user"))
    }
  }

  protected def apiResourceClass = this.getClass
}
