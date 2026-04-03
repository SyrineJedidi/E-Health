/**
 * Développement : URLs relatives + proxy (voir proxy.conf.json).
 * Le navigateur appelle http://localhost:4200/api/... → dev server proxy.
 * - /api/patients → patient-service (8081)
 * - /api/doctors, /api/rendezvous → gateway (8080)
 */
export const environment = {
  production: false,
  apiGatewayUrl: 'http://localhost:8080',
  patientServiceUrl: '/api/patients',
  doctorServiceUrl: '/api/doctors',
  appointmentServiceUrl: '/api/rendezvous'
};
