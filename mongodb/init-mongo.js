const appPassword = process.env.MONGO_APP_PASSWORD;

db = db.getSiblingDB('AdminService');
db.createUser({ user: 'admin_svc_user', pwd: appPassword, roles: [{ role: 'readWrite', db: 'AdminService' }] });

db = db.getSiblingDB('clinicadminservice');
db.createUser({ user: 'clinicadmin_svc_user', pwd: appPassword, roles: [{ role: 'readWrite', db: 'clinicadminservice' }] });

db = db.getSiblingDB('customerService');
db.createUser({ user: 'customer_svc_user', pwd: appPassword, roles: [{ role: 'readWrite', db: 'customerService' }] });

db = db.getSiblingDB('customerAppointments');
db.createUser({ user: 'booking_svc_user', pwd: appPassword, roles: [{ role: 'readWrite', db: 'customerAppointments' }] });

db = db.getSiblingDB('DermaCareNotifications');
db.createUser({ user: 'notification_svc_user', pwd: appPassword, roles: [{ role: 'readWrite', db: 'DermaCareNotifications' }] });

db = db.getSiblingDB('physiotherapydoctorservice');
db.createUser({ user: 'physio_svc_user', pwd: appPassword, roles: [{ role: 'readWrite', db: 'physiotherapydoctorservice' }] });

print('All service DB users created.');