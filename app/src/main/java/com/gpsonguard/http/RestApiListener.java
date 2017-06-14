package com.gpsonguard.http;

public interface RestApiListener {
		public void onResponse(String response);
		public void onHttpError(String errorMessage);
		public void onNetworkError(String networkError);
		public void noIternetConnectivity();
}
