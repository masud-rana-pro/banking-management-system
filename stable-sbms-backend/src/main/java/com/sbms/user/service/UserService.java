package com.sbms.user.service;

import com.sbms.branch.entity.Branch;
import com.sbms.branch.repository.BranchRepository;
import com.sbms.common.exception.BadRequestException;
import com.sbms.common.exception.ResourceNotFoundException;
import com.sbms.common.mail.AutomatedMailService;
import com.sbms.role.entity.Role;
import com.sbms.role.repository.RoleRepository;
import com.sbms.user.dto.*;
import com.sbms.user.entity.User;
import com.sbms.user.entity.UserRole;
import com.sbms.user.entity.UserSession;
import com.sbms.user.enums.UserStatus;
import com.sbms.user.enums.UserType;
import com.sbms.user.repository.UserRepository;
import com.sbms.user.repository.UserRoleRepository;
import com.sbms.user.repository.UserSessionRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private static final Set<String> BRANCH_BOUND_ROLES = Set.of(
            "BRANCH_MANAGER",
            "BRANCH_STAFF",
            "TELLER",
            "OPERATIONS_OFFICER",
            "INVESTMENT_OFFICER",
            "RECOVERY_OFFICER");

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BranchRepository branchRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserSessionRepository userSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final AutomatedMailService automatedMailService;

    public UserService(UserRepository userRepository,
            RoleRepository roleRepository,
            BranchRepository branchRepository,
            UserRoleRepository userRoleRepository,
            UserSessionRepository userSessionRepository,
            PasswordEncoder passwordEncoder,
            AutomatedMailService automatedMailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.branchRepository = branchRepository;
        this.userRoleRepository = userRoleRepository;
        this.userSessionRepository = userSessionRepository;
        this.passwordEncoder = passwordEncoder;
        this.automatedMailService = automatedMailService;
    }

    @Transactional(readOnly = true)
    public UserDashboardSummaryDto getDashboardSummary() {
        List<User> users = userRepository.findAll();
        UserDashboardSummaryDto dto = new UserDashboardSummaryDto();
        dto.setTotalUsers(userRepository.countAll());
        dto.setActiveUsers(userRepository.countByStatus("ACTIVE"));
        dto.setLockedUsers(userRepository.countLocked());
        dto.setUsersByRole(users.stream()
                .collect(Collectors.groupingBy(
                        user -> user.getRole() == null ? "Unassigned Role" : user.getRole().getName(),
                        Collectors.counting()))
                .entrySet().stream()
                .map(entry -> new UserBreakdownDto(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(UserBreakdownDto::getCount).reversed())
                .toList());
        dto.setUsersByBranch(users.stream()
                .collect(Collectors.groupingBy(user -> resolveBranchLabel(user.getBranchId()), Collectors.counting()))
                .entrySet().stream()
                .map(entry -> new UserBreakdownDto(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(UserBreakdownDto::getCount).reversed())
                .toList());
        dto.setRecentLogins(userSessionRepository.findRecent(8).stream()
                .map(session -> toDto(session.getUser()))
                .toList());
        return dto;
    }

    public UserResponseDto create(UserCreateRequestDto dto) {
        validateCreateOrUpdate(dto.getUsername(), dto.getEmail(), dto.getMobile(), dto.getRoleId(), dto.getBranchId(),
                dto.getUserType(), null);
        if (isBlank(dto.getPassword()))
            throw new BadRequestException("Password is required");

        Role role = getRole(dto.getRoleId());
        Branch branch = getBranchOptional(dto.getBranchId());

        User user = new User();
        user.setUserCode(generateUserCode());
        user.setUsername(dto.getUsername().trim());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setFullName(requireText(dto.getFullName(), "Full name is required"));
        user.setEmail(blankToNull(dto.getEmail()));
        user.setMobile(blankToNull(dto.getMobile()));
        user.setProfileImageName(blankToNull(dto.getProfileImageName()));
        user.setEmployeeNo(blankToNull(dto.getEmployeeNo()));
        user.setDesignation(blankToNull(dto.getDesignation()));
        user.setBranchId(branch == null ? null : branch.getId());
        user.setRole(role);
        user.setUserType(parseUserType(dto.getUserType(), role));
        applyStatusFlags(user, dto.getStatus(), dto.getActive(), dto.getLocked());
        user.setCreatedBy(actor(dto.getActionBy()));
        user.setUpdatedBy(actor(dto.getActionBy()));
        user.setMustChangePassword(true);
        userRepository.save(user);
        recordRoleAssignment(user, role, actor(dto.getActionBy()));
        if (!isBlank(user.getEmail())) {
            automatedMailService.sendUserWelcomeEmail(
                    user.getEmail(),
                    user.getFullName(),
                    user.getUsername(),
                    role == null ? "Unassigned Role" : role.getName());
        }
        return toDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAll(String search, String status, Long roleId, Long branchId) {
        return userRepository.findAll(search, status, roleId, branchId).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getDropdown() {
        return userRepository.findActiveDropdown().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> search(String keyword) {
        return userRepository.search(keyword).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public UserResponseDto getById(Long id) {
        return toDto(getUser(id));
    }

    public UserResponseDto update(Long id, UserUpdateRequestDto dto) {
        User user = getUser(id);
        Long nextRoleId = dto.getRoleId() != null ? dto.getRoleId()
                : user.getRole() == null ? null : user.getRole().getId();
        String nextUserType = dto.getUserType() != null ? dto.getUserType() : user.getUserType().name();
        Long nextBranchId = dto.getBranchId() != null ? dto.getBranchId() : user.getBranchId();
        validateCreateOrUpdate(
                dto.getUsername() == null ? user.getUsername() : dto.getUsername(),
                dto.getEmail() == null ? user.getEmail() : dto.getEmail(),
                dto.getMobile() == null ? user.getMobile() : dto.getMobile(),
                nextRoleId,
                nextBranchId,
                nextUserType,
                id);

        if (!isBlank(dto.getUsername()))
            user.setUsername(dto.getUsername().trim());
        if (!isBlank(dto.getFullName()))
            user.setFullName(dto.getFullName().trim());
        user.setEmail(dto.getEmail() == null ? user.getEmail() : blankToNull(dto.getEmail()));
        user.setMobile(dto.getMobile() == null ? user.getMobile() : blankToNull(dto.getMobile()));
        user.setProfileImageName(dto.getProfileImageName() == null ? user.getProfileImageName()
                : blankToNull(dto.getProfileImageName()));
        user.setEmployeeNo(dto.getEmployeeNo() == null ? user.getEmployeeNo() : blankToNull(dto.getEmployeeNo()));
        user.setDesignation(dto.getDesignation() == null ? user.getDesignation() : blankToNull(dto.getDesignation()));
        if (dto.getBranchId() != null || user.getBranchId() != null) {
            Branch branch = getBranchOptional(nextBranchId);
            user.setBranchId(branch == null ? null : branch.getId());
        }
        if (dto.getRoleId() != null) {
            Role role = getRole(dto.getRoleId());
            user.setRole(role);
            recordRoleAssignment(user, role, actor(dto.getActionBy()));
            if (role.getCode() != null && "CUSTOMER".equalsIgnoreCase(role.getCode())) {
                user.setUserType(UserType.CUSTOMER);
            }
        }
        if (dto.getUserType() != null) {
            user.setUserType(parseUserType(dto.getUserType(), user.getRole()));
        }
        if (dto.getEmailVerified() != null)
            user.setEmailVerified(dto.getEmailVerified());
        if (dto.getMobileVerified() != null)
            user.setMobileVerified(dto.getMobileVerified());
        applyStatusFlags(user, dto.getStatus(), dto.getActive(), dto.getLocked());
        user.setUpdatedBy(actor(dto.getActionBy()));
        userRepository.save(user);
        return toDto(user);
    }

    public UserResponseDto deactivate(Long id) {
        User user = getUser(id);
        user.setStatus(UserStatus.INACTIVE);
        user.setActive(false);
        user.setLocked(false);
        user.setUpdatedBy("SYSTEM");
        userRepository.save(user);
        return toDto(user);
    }

    public UserResponseDto restore(Long id) {
        User user = getUser(id);
        user.setStatus(UserStatus.ACTIVE);
        user.setActive(true);
        user.setLocked(false);
        user.setLockedAt(null);
        user.setUpdatedBy("SYSTEM");
        userRepository.save(user);
        return toDto(user);
    }

    public UserResponseDto lock(Long id, UserLockActionDto dto) {
        User user = getUser(id);
        user.setStatus(UserStatus.LOCKED);
        user.setLocked(true);
        user.setActive(false);
        user.setLockedAt(LocalDateTime.now());
        user.setUpdatedBy(actor(dto == null ? null : dto.getActionBy()));
        userRepository.save(user);
        return toDto(user);
    }

    public UserResponseDto unlock(Long id, UserLockActionDto dto) {
        User user = getUser(id);
        user.setStatus(UserStatus.ACTIVE);
        user.setLocked(false);
        user.setActive(true);
        user.setLockedAt(null);
        user.setFailedLoginCount(0);
        user.setUpdatedBy(actor(dto == null ? null : dto.getActionBy()));
        userRepository.save(user);
        return toDto(user);
    }

    public UserResponseDto resetPassword(Long id, UserPasswordResetDto dto) {
        if (dto == null || isBlank(dto.getNewPassword()))
            throw new BadRequestException("New password is required");
        if (!dto.getNewPassword().equals(dto.getConfirmPassword()))
            throw new BadRequestException("Password confirmation does not match");
        User user = getUser(id);
        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        user.setPasswordChangedAt(LocalDateTime.now());
        user.setMustChangePassword(true);
        user.setUpdatedBy(actor(dto.getActionBy()));
        userRepository.save(user);
        if (!isBlank(user.getEmail())) {
            automatedMailService.sendAdminPasswordResetEmail(
                    user.getEmail(),
                    user.getFullName(),
                    user.getUsername());
        }
        return toDto(user);
    }

    public UserResponseDto assignRole(Long id, UserRoleAssignDto dto) {
        if (dto == null || dto.getRoleId() == null)
            throw new BadRequestException("Role is required");
        User user = getUser(id);
        Role role = getRole(dto.getRoleId());
        validateCustomerRole(role, user.getUserType());
        if (requiresBranch(role) && user.getBranchId() == null) {
            throw new BadRequestException("Branch is required before assigning this role");
        }
        user.setRole(role);
        user.setUpdatedBy(actor(dto.getActionBy()));
        userRepository.save(user);
        recordRoleAssignment(user, role, actor(dto.getActionBy()));
        return toDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserHistoryEntryDto> getHistory(Long id) {
        User user = getUser(id);
        List<UserHistoryEntryDto> roleEntries = userRoleRepository.findByUserId(id).stream().map(item -> {
            UserHistoryEntryDto dto = new UserHistoryEntryDto();
            dto.setEventType("ROLE_ASSIGNMENT");
            dto.setRoleCode(item.getRole().getCode());
            dto.setRoleName(item.getRole().getName());
            dto.setRemarks("Role mapped to user");
            dto.setCreatedAt(item.getCreatedAt());
            return dto;
        }).toList();
        List<UserHistoryEntryDto> sessionEntries = userSessionRepository.findByUserId(id).stream().map(item -> {
            UserHistoryEntryDto dto = new UserHistoryEntryDto();
            dto.setEventType("LOGIN_SESSION");
            dto.setStatus(item.getStatus());
            dto.setIpAddress(item.getIpAddress());
            dto.setDeviceInfo(item.getDeviceInfo());
            dto.setLoginTime(item.getLoginTime());
            dto.setLogoutTime(item.getLogoutTime());
            dto.setCreatedAt(item.getLoginTime());
            return dto;
        }).toList();
        return java.util.stream.Stream.concat(roleEntries.stream(), sessionEntries.stream())
                .sorted(Comparator
                        .comparing(UserHistoryEntryDto::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()))
                        .reversed())
                .toList();
    }

    private void validateCreateOrUpdate(String username,
            String email,
            String mobile,
            Long roleId,
            Long branchId,
            String userType,
            Long exceptId) {
        if (isBlank(username))
            throw new BadRequestException("Username is required");
        if (userRepository.existsByUsername(username, exceptId))
            throw new BadRequestException("Username already exists");
        if (!isBlank(email) && userRepository.existsByEmail(email, exceptId))
            throw new BadRequestException("Email already exists");
        if (!isBlank(mobile) && userRepository.existsByMobile(mobile, exceptId))
            throw new BadRequestException("Mobile already exists");
        Role role = roleId == null ? null : getRole(roleId);
        if (role != null) {
            validateCustomerRole(role, parseUserType(userType, role));
            if (requiresBranch(role) && branchId == null) {
                throw new BadRequestException("Branch is required for branch staff roles");
            }
            if (branchId != null)
                getBranchOptional(branchId);
        }
    }

    private void validateCustomerRole(Role role, UserType userType) {
        if (role != null && role.getCode() != null && "CUSTOMER".equalsIgnoreCase(role.getCode())
                && userType != UserType.CUSTOMER) {
            throw new BadRequestException("Customer role can only be assigned to customer users");
        }
    }

    private boolean requiresBranch(Role role) {
        return role != null && role.getCode() != null
                && BRANCH_BOUND_ROLES.contains(role.getCode().trim().toUpperCase(Locale.ROOT));
    }

    private Branch getBranchOptional(Long branchId) {
        if (branchId == null)
            return null;
        return branchRepository.findById(branchId).orElseThrow(() -> new BadRequestException("Invalid branch"));
    }

    private Role getRole(Long roleId) {
        return roleRepository.findById(roleId).orElseThrow(() -> new BadRequestException("Invalid role"));
    }

    private User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private UserType parseUserType(String value, Role role) {
        if (role != null && role.getCode() != null && "CUSTOMER".equalsIgnoreCase(role.getCode())) {
            return UserType.CUSTOMER;
        }
        if (isBlank(value))
            return UserType.STAFF;
        try {
            return UserType.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid user type");
        }
    }

    private void applyStatusFlags(User user, String statusValue, Boolean activeValue, Boolean lockedValue) {
        UserStatus status;
        if (lockedValue != null && lockedValue) {
            status = UserStatus.LOCKED;
        } else if (!isBlank(statusValue)) {
            try {
                status = UserStatus.valueOf(statusValue.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ex) {
                throw new BadRequestException("Invalid user status");
            }
        } else {
            status = Boolean.FALSE.equals(activeValue) ? UserStatus.INACTIVE : UserStatus.ACTIVE;
        }
        user.setStatus(status);
        user.setLocked(status == UserStatus.LOCKED || Boolean.TRUE.equals(lockedValue));
        user.setActive(
                status == UserStatus.ACTIVE && !Boolean.TRUE.equals(lockedValue) && !Boolean.FALSE.equals(activeValue));
        if (user.getLocked()) {
            user.setLockedAt(LocalDateTime.now());
        } else if (status != UserStatus.LOCKED) {
            user.setLockedAt(null);
        }
    }

    private void recordRoleAssignment(User user, Role role, String createdBy) {
        UserRole assignment = new UserRole();
        assignment.setUser(user);
        assignment.setRole(role);
        assignment.setCreatedBy(createdBy);
        userRoleRepository.save(assignment);
    }

    private String generateUserCode() {
        long next = userRepository.countAll() + 1;
        return "USR-" + String.format("%05d", next);
    }

    private UserResponseDto toDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setUserCode(user.getUserCode());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setMobile(user.getMobile());
        dto.setProfileImageName(user.getProfileImageName());
        dto.setEmployeeNo(user.getEmployeeNo());
        dto.setDesignation(user.getDesignation());
        dto.setBranchId(user.getBranchId());
        dto.setUserType(user.getUserType() == null ? null : user.getUserType().name());
        dto.setStatus(user.getStatus() == null ? null : user.getStatus().name());
        dto.setActive(Boolean.TRUE.equals(user.getActive()));
        dto.setLocked(Boolean.TRUE.equals(user.getLocked()));
        dto.setEmailVerified(Boolean.TRUE.equals(user.getEmailVerified()));
        dto.setMobileVerified(Boolean.TRUE.equals(user.getMobileVerified()));
        dto.setFailedLoginCount(user.getFailedLoginCount());
        dto.setLastLoginAt(user.getLastLoginAt());
        dto.setLockedAt(user.getLockedAt());
        dto.setPasswordChangedAt(user.getPasswordChangedAt());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setCreatedBy(user.getCreatedBy());
        dto.setUpdatedBy(user.getUpdatedBy());
        if (user.getRole() != null) {
            dto.setRoleId(user.getRole().getId());
            dto.setRoleCode(user.getRole().getCode());
            dto.setRoleName(user.getRole().getName());
        }
        Branch branch = user.getBranchId() == null ? null : branchRepository.findById(user.getBranchId()).orElse(null);
        if (branch != null) {
            dto.setBranchCode(branch.getBranchCode());
            dto.setBranchName(branch.getBranchName());
        }
        dto.setMappedRoles(userRoleRepository.findByUserId(user.getId()).stream()
                .map(item -> item.getRole().getCode() + " - " + item.getRole().getName())
                .distinct()
                .toList());
        dto.setHistoryCount((int) (userRoleRepository.countByUserId(user.getId())
                + userSessionRepository.countByUserId(user.getId())));
        return dto;
    }

    private String resolveBranchLabel(Long branchId) {
        if (branchId == null)
            return "Unassigned Branch";
        return branchRepository.findById(branchId)
                .map(branch -> branch.getBranchCode() + " - " + branch.getBranchName())
                .orElse("Unknown Branch");
    }

    private String actor(String actionBy) {
        return isBlank(actionBy) ? "SYSTEM" : actionBy.trim();
    }

    private String requireText(String value, String message) {
        if (isBlank(value))
            throw new BadRequestException(message);
        return value.trim();
    }

    private String blankToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

}
