package org.processbase.ui.bpm.identity.sync;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.util.Hash;
import org.ow2.bonita.facade.identity.Group;
import org.ow2.bonita.facade.identity.Membership;
import org.ow2.bonita.facade.identity.Role;
import org.ow2.bonita.facade.identity.User;
import org.ow2.bonita.util.BonitaConstants;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.ProcessbaseApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User roles sync.
 * 
 * @author Margo
 */
public class UserRolesSync {

	/** Logger. */
	private static final Logger LOG = LoggerFactory
			.getLogger(UserRolesSync.class);

	private static final String ROLE_USER = "user";
	private static final String GROUP_PLATFORM = "platform";

	private static final String KOVMEN_ROLE = "KOVMEN";

	private BPMModule bpmModule;

	private Properties properties;

	private String organization;

	public UserRolesSync() {

		bpmModule = ProcessbaseApplication.getCurrent().getBpmModule();

		String domain = bpmModule.getCurrentDomain();

		String homeDir = BonitaConstants.getBonitaHomeFolder();
		File file = new File(homeDir + "/server/" + domain
				+ "/conf/aar.properties");
		
		LOG.warn("conf:" + homeDir + "/server/" + domain
				+ "/conf/aar.properties");

		FileInputStream in = null;
		try {
			in = new FileInputStream(file);

			properties = new Properties();
			properties.load(in);

		} catch (Exception e) {
			LOG.warn("could not read properties", e);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
			}
		}

		try {
			organization = properties.getProperty("organization.regCode");
		} catch (Exception e) {
			LOG.warn("could not get organization", e);
		}
	}

	/**
	 * Update user roles.
	 * 
	 * @param user
	 *            user.
	 */
	public void updateUser(User user) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("updateUser() user: " + user.getUsername()
					+ " organization: " + organization);
		}
		
		//Find user
		try {
			user = bpmModule.findUserByUserName(user.getUsername());
		} catch (Exception e) {
			LOG.warn("could not find user", e);
			return;
		}

		if (StringUtils.isBlank(organization)) {
			LOG.warn("property 'organization.regCode' set");
			return;
		}

		// Current memberships
		Set<String> current = new HashSet<String>();

		// New memberships
		Set<String> memberships = new HashSet<String>();

		for (Membership m : user.getMemberships()) {
			current.add(m.getUUID());
			if (m.getRole().getName().equals(ROLE_USER)
					|| m.getGroup().getName().equals(GROUP_PLATFORM)) {
				memberships.add(m.getUUID());
			}
		}

		// Find user current positions
		List<Position> positions = findUserPositions(user.getUsername());

		// New temporary positions
		List<Position> newPositions = new ArrayList<Position>();

		// For each position
		for (Position position : positions) {

			// Check if organization matches
			if (!organization.equalsIgnoreCase(position.getOrganization())) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("skipping organization "
							+ position.getOrganization());
				}
				continue;
			}

			boolean hasAllRoles = true;

			for (String r : position.getRoles()) {

				// If is KOVMEN role
				if (!r.startsWith(KOVMEN_ROLE)) {
					continue;
				}

				// Find group and role
				String[] tmp = r.split("\\.");
				if (tmp.length < 3) {
					continue;
				}

				List<String> groupNames = new ArrayList<String>();
				for (int i = 1; i < tmp.length - 1; i++) {
					groupNames.add(tmp[i]);
				}

				String roleName = tmp[tmp.length - 1];

				try {
					// Find role
					Role role = bpmModule.findRoleByName(roleName);
					if (role == null) {
						role = bpmModule.addRole(roleName, roleName, "");
					}

					// Find group
					Group group = null;
					Group parent = null;

					for (String groupName : groupNames) {
						group = bpmModule.findGroupByName(groupName,
								parent != null ? parent.getName() : null);
						if (group == null) {
							group = bpmModule.addGroup(groupName, groupName, "AUTO IMPORTED",
									parent != null ? parent.getUUID() : null);
						}
						parent = group;
					}

					// Find membership
					Membership membership = bpmModule
							.getMembershipForRoleAndGroup(role.getUUID(),
									group.getUUID());

					memberships.add(membership.getUUID());

					// User this is new membership
					if (!current.contains(membership.getUUID())) {
						hasAllRoles = false;
					}

				} catch (Exception e) {
					LOG.warn("could not find membership", e);
				}

			}

			// If is new position
			if (!hasAllRoles) {
				newPositions.add(position);
			}

		}

		// Check if memberships are same
		boolean changed = memberships.size() != current.size();
		if (!changed) {
			for (String c : current) {
				if (!memberships.contains(c)) {
					changed = true;
				}
			}
		}

		//If memberships have changed
		if (changed) {

			if (LOG.isDebugEnabled()) {
				LOG.debug("memberships have changed");
			}

			// Update user memberships
			try {
				bpmModule.setUserMemberships(user.getUUID(), memberships);
			} catch (Exception e) {
				LOG.warn("could not update memberships", e);
			}
			
			//Users form who to remove assigned tasks
			Set<String> changeAssigned = new HashSet<String>();
			
			// If there are new positions
			if (newPositions.size() > 0) {

				for (Position position : newPositions) {
					if (!position.getType().equals(Position.TYPE_AS)) {
						continue;
					}
					String fullTime = findFullTimeEmployee(position);
					if (fullTime != null) {
						if (LOG.isDebugEnabled()) {
							LOG.debug("found full time employee " + fullTime
									+ " for position " + position.getName());
						}
						changeAssigned.add(fullTime);
					}
				}

			}


			//Start update candidates list task
			new UpdateCanditadeListsTask(user.getUsername(), changeAssigned).start();
			
			
		}else{
			if (LOG.isDebugEnabled()) {
				LOG.debug("memberships are same");
			}
		}

	}

	/**
	 * Find user positions.
	 * 
	 * @param username
	 *            user name (ID code).
	 * @return positions list.
	 */
	private List<Position> findUserPositions(String username) {

		List<Position> positions = new ArrayList<Position>();

		Connection connection = getConnection();
		if (connection != null) {

			StringBuilder q = new StringBuilder();
			q.append(
					"select a.ametikoht_id, a.ametikoht_nimetus, asutus.registrikood, o.roll, t.roll from asutused.ametikoht a ")
					.append("inner join asutused.asutus asutus on a.asutus_id=asutus.asutus_id ")
					.append("inner join asutused.ametikoht_taitmine t on t.ametikoht_id=a.ametikoht_id and COALESCE(t.alates, current_date) <= COALESCE(a.kuni, current_date) and COALESCE(t.kuni, current_date) >= COALESCE(a.alates, current_date) ")
					.append("inner join asutused.oigus_antud o on o.asutus_id=asutus.asutus_id and o.ametikoht_id=a.ametikoht_id and COALESCE(o.alates, current_date) <= current_date and COALESCE(o.kuni, current_date) >= current_date ")
					.append("inner join asutused.isik i on t.i_id=i.i_id where i.kood= ? ");

			Map<Integer, Position> positionsMap = new HashMap<Integer, Position>();

			// Find user
			PreparedStatement s = null;
			try {
				s = connection.prepareStatement(q.toString());
				s.setString(1, username);

				ResultSet rs = s.executeQuery();
				while (rs.next()) {

					Integer id = rs.getInt(1);
					String name = rs.getString(2);
					String org = rs.getString(3);
					String role = rs.getString(4);
					String type = rs.getString(5);

					Position position = positionsMap.get(id);
					if (position == null) {
						position = new Position();
						position.setId(id);
						position.setName(name);
						position.setOrganization(org);
						position.setType(type);
						positionsMap.put(id, position);
					}
					position.getRoles().add(role);

				}
				rs.close();
			} catch (Exception e) {
				LOG.warn("could not find user positions in AAR", e);
			}

			positions.addAll(positionsMap.values());

			releaseConnection(connection);
		} else {
			LOG.warn("could not get connection");
		}

		return positions;
	}

	/**
	 * Find position full time employee.
	 * 
	 * @param position
	 *            position.
	 * @return
	 */
	public String findFullTimeEmployee(Position position) {

		Connection connection = getConnection();
		if (connection != null) {

			StringBuilder q = new StringBuilder();
			q.append("select i.kood from asutused.ametikoht a ")
					.append("inner join asutused.asutus asutus on a.asutus_id=asutus.asutus_id ")
					.append("inner join asutused.ametikoht_taitmine t on t.ametikoht_id=a.ametikoht_id ")
					.append("and COALESCE(t.alates, current_date) <= COALESCE(a.kuni, current_date) ")
					.append("and t.roll = 'PK' ")
					.append("inner join asutused.oigus_antud o on o.asutus_id=asutus.asutus_id ")
					.append("and o.ametikoht_id=a.ametikoht_id ")
					.append("and COALESCE(o.alates, current_date) <= current_date and COALESCE(o.kuni, current_date) >= current_date ")
					.append("inner join asutused.isik i on t.i_id=i.i_id where a.ametikoht_id = ? order by t.kuni ");

			// Find user
			PreparedStatement s = null;
			try {
				s = connection.prepareStatement(q.toString());
				s.setInt(1, position.getId());

				ResultSet rs = s.executeQuery();

				while (rs.next()) {
					return rs.getString(1);
				}

				rs.close();
			} catch (Exception e) {
				LOG.warn("could not find", e);
			} finally {
				releaseConnection(connection);
			}

		} else {
			LOG.warn("could not get connection");
		}

		return null;
	}

	/**
	 * Get database connection. Call {@link #releaseConnection(Connection)} to
	 * close connection.
	 * 
	 * @return {@link Connection} or <code>null</code> when error occured.
	 */
	private Connection getConnection() {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			LOG.warn("driver not found");
		}
		try {
			String dbHost = properties.getProperty("db.aar.aaddress");
			String dbName = properties.getProperty("db.aar.dbname");
			String dbUser = properties.getProperty("db.aar.username");
			String dbPass = properties.getProperty("db.aar.password");

			return DriverManager
					.getConnection(
							"jdbc:postgresql://"
									+ dbHost
									+ "/"
									+ dbName
									+ "?autoReconnect=true&amp;useUnicode=true&amp;characterEncoding=UTF-8",
							dbUser, dbPass);
		} catch (Exception e) {
			LOG.warn("could not get connect", e);
		}
		return null;
	}

	/**
	 * Close obtained connection.
	 * 
	 * @param connection
	 *            connection to close.
	 */
	private void releaseConnection(Connection connection) {
		// Release connection
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (Exception e) {
			LOG.warn("could not release connection", e);
		}
	}

}
