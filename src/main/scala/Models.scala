import spray.json.DefaultJsonProtocol

/* Copyright (C) 2016 Synergy-GB LLC.
 * All rights reserved.
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 */
/**
  * Created by saul on 2/16/16.
  */
object Models extends DefaultJsonProtocol {

  implicit val registrationFormat = jsonFormat3(Registration)
  case class Registration(username : String, sharedSecret : String, complete : Boolean = false)
}
