/**
 * Toutes les requêtes /api/* sont proxifiées vers le backend.
 * Par défaut : API Gateway (8085) — 8080 souvent pris par un service Windows (svchost).
 * Surcharge : $env:EHEALTH_API_PROXY="http://localhost:8080"; npm start
 * Sans gateway, uniquement patient-service sur 8091 :
 *   $env:EHEALTH_API_PROXY="http://localhost:8091"; npm start
 */
const target = process.env.EHEALTH_API_PROXY || 'http://localhost:8085';

module.exports = {
  '/api': {
    target,
    secure: false,
    changeOrigin: true,
    logLevel: 'warn',
  },
};
