/**
 * Développement : URLs relatives + proxy (voir proxy.conf.js).
 * Par défaut tout /api/* → API Gateway (proxy → http://localhost:8085 si 8080 occupé).
 * Démarrer au minimum : eureka-server, patient-service, api-gateway.
 * Option direct patient sans gateway : EHEALTH_API_PROXY=http://localhost:8091 npm start
 */
export const environment = {
  production: false,
  apiGatewayUrl: 'http://localhost:8085',
  authBaseUrl: '/api/auth',
  patientServiceUrl: '/api/patients',
  prescriptionServiceUrl: '/api/prescriptions',
  medicationServiceUrl: '/api/medications',
  doctorServiceUrl: '/api/doctors',
  /** Spécialités (doctor-service) — route gateway dédiée */
  specialtiesServiceUrl: '/api/specialties',
  appointmentServiceUrl: '/api/rendezvous'
};
