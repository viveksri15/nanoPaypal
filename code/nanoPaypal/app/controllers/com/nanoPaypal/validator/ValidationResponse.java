package controllers.com.nanoPaypal.validator;

public class ValidationResponse {
	private INVALID_MESSAGE invalid_MESSAGE;
	private boolean isSuccess;
	private Object resultObj;
	public ValidationResponse(boolean isSuccess,
			INVALID_MESSAGE invalid_MESSAGE, Object object) {
		this.invalid_MESSAGE = invalid_MESSAGE;
		this.isSuccess = isSuccess;
		this.resultObj = object;
	}
	public INVALID_MESSAGE getInvalid_MESSAGE() {
		return invalid_MESSAGE;
	}
	public boolean isSuccess() {
		return isSuccess;
	}
	public Object getResultObj() {
		return resultObj;
	}
}
