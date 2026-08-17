package com.berkay.auth_service.personnel.config;

import com.berkay.auth_service.personnel.entity.PermissionGroup;
import com.berkay.auth_service.personnel.entity.Personnel;
import com.berkay.auth_service.personnel.repository.PermissionGroupRepository;
import com.berkay.auth_service.personnel.repository.PersonnelRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seeds exactly one P0 (Permissions page, RULES.md) personnel account on first startup so
 * there's someone able to use the Permissions CRUD API (task 1.3) to create every other
 * personnel/group - without this, nobody could ever create the first admin account. Only runs
 * when the personnel table is completely empty; never touches it again afterwards.
 */
@Configuration
@EnableConfigurationProperties(PersonnelBootstrapProperties.class)
public class PersonnelBootstrapRunner implements ApplicationRunner {

	private static final String PERMISSIONS_PAGE_CODE = "P0";
	private static final String SUPER_ADMIN_GROUP_NAME = "Super Admin";

	private final Log log = LogFactory.getLog(getClass());

	private final PersonnelRepository personnelRepository;
	private final PermissionGroupRepository permissionGroupRepository;
	private final PasswordEncoder passwordEncoder;
	private final PersonnelBootstrapProperties properties;

	public PersonnelBootstrapRunner(PersonnelRepository personnelRepository,
			PermissionGroupRepository permissionGroupRepository, PasswordEncoder passwordEncoder,
			PersonnelBootstrapProperties properties) {
		this.personnelRepository = personnelRepository;
		this.permissionGroupRepository = permissionGroupRepository;
		this.passwordEncoder = passwordEncoder;
		this.properties = properties;
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		if (personnelRepository.count() > 0) {
			return;
		}
		if (properties.getEmail() == null || properties.getPassword() == null) {
			log.warn("No personnel accounts exist and berkay.personnel.bootstrap.email/password "
					+ "are not set - skipping bootstrap. No one will be able to log into the personnel panel.");
			return;
		}

		PermissionGroup superAdminGroup = permissionGroupRepository.findByName(SUPER_ADMIN_GROUP_NAME)
				.orElseGet(() -> permissionGroupRepository.save(new PermissionGroup(SUPER_ADMIN_GROUP_NAME)));
		superAdminGroup.grant(PERMISSIONS_PAGE_CODE, true, true, true);
		permissionGroupRepository.save(superAdminGroup);

		Personnel bootstrapPersonnel = new Personnel(
				properties.getEmail().trim().toLowerCase(),
				passwordEncoder.encode(properties.getPassword()),
				"Super",
				"Admin",
				superAdminGroup);
		personnelRepository.save(bootstrapPersonnel);

		log.info("Bootstrapped the first personnel account (" + bootstrapPersonnel.getEmail()
				+ ") in the '" + SUPER_ADMIN_GROUP_NAME + "' permission group.");
	}
}
