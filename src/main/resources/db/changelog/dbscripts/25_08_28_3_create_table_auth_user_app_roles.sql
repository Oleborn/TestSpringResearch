CREATE table auth_user_roles(
    auth_user_id BIGINT NOT NULL,
    role VARCHAR(255) NOT NULL,

    CONSTRAINT pk_auth_user_roles PRIMARY KEY (auth_user_id, role),
    constraint fk_auth_user_roles_user foreign key (auth_user_id) references auth_user(id) ON DELETE CASCADE
)