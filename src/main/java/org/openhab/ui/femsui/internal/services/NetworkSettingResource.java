package org.openhab.ui.femsui.internal.services;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eclipse.smarthome.io.rest.RESTResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("fems/setting/network")
public class NetworkSettingResource implements RESTResource {

	private static final Logger logger = LoggerFactory.getLogger(NetworkSettingResource.class);
	//private static final String interfacesFile = "/etc/network/interfaces";
	private static final String interfacesFile = "D:/Desktop/interfaces.txt";
	private static final Charset charset = StandardCharsets.UTF_8;
	private static final String staticConfigurationText = ""
			+ "# This file was created by FEMS UI\n"
			+ "# Do not change contents manually!\n"
			+ "\n"
			+ "source /etc/network/interfaces.d/*\n"
			+ ""
			+ "# The loopback network interface\n"
			+ "auto lo\n"
			+ "iface lo inet loopback\n"
			+ "\n"
			+ "# The primary network interface\n"
			+ "allow-hotplug eth0\n";
	
	@Context
	UriInfo uriInfo;

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response httpGetModelList(@Context HttpHeaders headers) throws UnknownHostException {
		logger.debug("Received HTTP GET request at '{}'", uriInfo.getPath());

		NetworkSettingBean networkSetting; 
		try {
			networkSetting = getCurrentNetworkSetting();
		} catch (IOException e) {
			e.printStackTrace();
			networkSetting = new NetworkSettingBean();
		}
		
		/*NetworkSettingBean networkSetting = new NetworkSettingBean();
		networkSetting.mode = NetworkSettingBean.Mode.STATIC;
		networkSetting.gateway = (Inet4Address) Inet4Address.getByName("192.168.0.1");
		networkSetting.netmask = (Inet4Address) Inet4Address.getByName("255.255.255.0");
		networkSetting.ip = (Inet4Address) Inet4Address.getByName("192.168.0.10");*/
		
		return Response.ok(networkSetting).build();
	}
	
	@PUT
	@Produces({ MediaType.APPLICATION_JSON })
	public Response httpPutModelSource(@Context HttpHeaders headers, NetworkSettingBean networkSetting) {
		logger.debug("Received HTTP PUT request at '{}'", uriInfo.getPath());
		try {
			setNetworkSetting(networkSetting);
			return Response.ok().build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.serverError().build();
		}	
	}
	
	/**
	 * Read the current network settings from network configuration file
	 * 
	 * @return Current network setting
	 * @throws IOException on read error
	 */
	public NetworkSettingBean getCurrentNetworkSetting() throws IOException {
		NetworkSettingBean networkSetting = new NetworkSettingBean();
		List<String> lines = Files.readAllLines(Paths.get(interfacesFile), charset);
		boolean inEth0Block = false;
		for(String line : lines) {
			// Get block values
			if(inEth0Block) {
				System.out.println(line);
				String[] splitLine = line.trim().split(" ");
				if(splitLine.length > 1) {
					System.out.println(splitLine[0]);
					System.out.println(splitLine[1]);
					if(splitLine[0].equals("address")) {
						networkSetting.ip = (Inet4Address) Inet4Address.getByName(splitLine[1]);
					} else if(splitLine[0].equals("netmask")) {
						networkSetting.netmask = (Inet4Address) Inet4Address.getByName(splitLine[1]);
					} else if(splitLine[0].equals("gateway")) {
						networkSetting.gateway = (Inet4Address) Inet4Address.getByName(splitLine[1]);
					}
				}
			}
			// Find beginning of block
			if(line.startsWith("iface ")) {
				if(line.startsWith("iface eth0 inet ")) {
					inEth0Block = true;
					if(line.substring("iface eth0 inet ".length()).trim().equals("dhcp")) {
						networkSetting.mode = NetworkSettingBean.Mode.DYNAMIC;
					} else { // "static"
						networkSetting.mode = NetworkSettingBean.Mode.STATIC;
					}
				} else {
					inEth0Block = false;
				}
			}
		}
		return networkSetting;
	}
	
	/**
	 * Write the network settings to network configuration file and activate them
	 * 
	 * @param networkSetting
	 * @throws IOException on write error or missing arguments
	 */
	public void setNetworkSetting(NetworkSettingBean networkSetting) throws IOException {
		// TODO: Write /etc/fems to avoid dhclient
		final String customConfigurationText;
		if(networkSetting.mode.equals(NetworkSettingBean.Mode.DYNAMIC)) {
			customConfigurationText = "iface eth0 inet dhcp\n";
		} else { // static
			if(networkSetting.ip == null || networkSetting.netmask == null || networkSetting.gateway == null) {
				throw new IllegalArgumentException("Alle Adressen müssen angegeben werden!");
			}
			customConfigurationText = String.format(""
					+ "iface eth0 inet static\n"
					+ "    address %s\n"
					+ "    netmask %s\n"
					+ "    gateway %s\n", networkSetting.ip.getHostAddress(), networkSetting.netmask.getHostAddress(), networkSetting.gateway.getHostAddress()); 
		}
		Files.write(Paths.get(interfacesFile), (staticConfigurationText + customConfigurationText).getBytes());
		// TODO: systemctl... networking service neu starten
	}
}
