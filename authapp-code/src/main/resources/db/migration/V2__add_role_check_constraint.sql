alter table users
    add constraint chk_users_role
    check (role in ('USER', 'ADMIN'));
