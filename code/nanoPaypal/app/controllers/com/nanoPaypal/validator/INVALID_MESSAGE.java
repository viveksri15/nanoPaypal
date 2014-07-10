package controllers.com.nanoPaypal.validator;

public enum INVALID_MESSAGE {
	SUCCESS,
	ERROR_badUserNamePassword, 
	ERROR_userOrEmailExists,
	ERROR_notMatchingPassword,
	ERROR_permissionDenied,
	ERROR_general, 
	ERROR_blankUserNameOrPassword, 
	ERROR_incompleteForm, 
	ERROR_invalidEmail,
	ERROR_noUserFound, 
	ERROR_invalidPassword
}