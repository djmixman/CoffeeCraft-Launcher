package pl.asiekierka.AsieLauncher.auth;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class AuthenticationMojangLegacy extends Authentication {

	public AuthenticationMojangLegacy() {
	}
	
	@Override
	public boolean requiresPassword() {
		return true;
	}

	@Override
	public int authenticate(String username, String password) {
		try {
			URL url = new URL("https://login.minecraft.net");
			HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			String params = "user="+username+"&password="+password+"&version=13";
			con.setDoOutput(true);
			DataOutputStream dos = new DataOutputStream(con.getOutputStream());
			dos.writeBytes(params);
			dos.flush();
			dos.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			inputLine = in.readLine();
			in.close();
			if(inputLine.length() == 0) return SERVER_DOWN;
			String[] output = inputLine.split(":");
			if(output.length < 5) { // Bad login, User not premium, etc
				error = inputLine;
				return LOGIN_INVALID;
			}
			realUsername = output[2];
			sessionID = output[3];
			return OK;
		} catch(Exception e) { e.printStackTrace(); error = "Internal AsieLauncher error"; return GENERAL_ERROR; }
	}

}

