/*
 *  Copyright 2010 demchuck.dima@gmail.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package gallery.web.validator.photo;

import common.CommonAttributes;
import common.bind.CommonBindValidator;
import gallery.model.beans.Photo;
import gallery.service.photo.IPhotoService;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.springframework.validation.Errors;
import security.beans.User;

/**
 * prevents user from updating items of other user
 * @author demchuck.dima@gmail.com
 */
public class PhotoUpdateBindValidator extends CommonBindValidator{
	public IPhotoService photo_service;

	public void init(){
		StringBuilder sb = new StringBuilder();
		common.utils.MiscUtils.checkNotNull(photo_service, "photo_service", sb);
		if (sb.length()>0){
			throw new NullPointerException(sb.toString());
		}
	}

	public static final String[] WHERE_CONDITION = new String[]{"id","id_users"};
	@Override
	protected void validate(Object command, Errors err, HttpServletRequest request) {
		super.validate(command, err, request);
		if (!err.hasErrors()){
			Photo p = (Photo) command;
			p.setDate_upload(new Date());
			validate(p.getId(), err, request, photo_service);
		}
	}

	/**
	 * validates a photo with given id
	 * @param id_photo photo's id
	 * @param err object with errors
	 * @param request http request
	 * @param photo_service service
	 * @return false if another user
	 */
	public static boolean validate(Long id_photo, Errors err, HttpServletRequest request, IPhotoService photo_service){
		User u = security.Utils.getCurrentUser(request);
		if (photo_service.getRowCount(WHERE_CONDITION, new Object[]{id_photo,u.getId()})!=1){
			//TODO try to show global errors
			if (err!=null)err.reject("another_user");
			CommonAttributes.addErrorMessage("another_user", request);
			return false;
		} else{
			return true;
		}
	}

	public void setPhoto_service(IPhotoService value){this.photo_service = value;}

}
