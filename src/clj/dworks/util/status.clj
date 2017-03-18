(ns dworks.util.status)

;; 1xx Informational
(def continue 100)
(def switching-protocols 101)
(def processing 102)

;; 2×× Success
(def ok 200)
(def created 201)
(def accepted 202)
(def no-content 203)
(def non-authoritative-information 204)
(def reset-content 205)
(def partial-content 206)
(def multi-status 207)
(def already-reported 208)

;; 3×× Redirection
(def multiple-choices 300)
(def moved-permanently 301)
(def found 302)
(def see-other 303)
(def not-modified 304)
(def user-proxy 305)
(def temporary-redirect 307)
(def permanent-redirect 308)

;; 4×× Client Error
(def bad-request 400)
(def unauthorized 401)
(def payment-required 402)
(def forbidden 403)
(def not-found 404)
(def method-not-allowed 405)
(def not-acceptable 406)
(def proxy-authentication-required 407)
(def request-timeout 408)
(def conflict 409)
(def gone 410)
(def length-required 411)
(def precondition-failed 412)
(def payload-too-large 413)
(def request-uri-too-long 414)
(def unsupported-media-type 415)
(def requested-range-not-satisfiable 416)
(def expectation-failed 417)
(def im-a-teapot 418)
(def misdirected-request 421)
(def unprocessable-entity 422)
(def locked 423)
(def failed-dependency 424)
(def upgrade-required 426)
(def preconditioned-required 428)
(def too-many-requests 429)
(def request-header-fields-too-large 431)
(def connection-closed-without-response 444)
(def unavailable-for-legal-reasons 451)
(def client-closed-request 499)

;; 5×× Server Error
(def internal-server-error 500)
(def not-implemented 501)
(def bad-gateway 502)
(def service-unavailable 503)
(def gateway-timeout 504)
(def http-version-not-supported 505)
(def variant-also-negotiates 506)
(def insufficient-storage 507)
(def loop-detected 508)
(def not-extended 510)
(def network-authentication-required 511)
(def network-connect-timeout-error 599)

(def informationals [continue switching-protocols processing])

(def successes [ok created accepted no-content non-authoritative-information
                reset-content partial-content multi-status already-reported])

(def redirections [multiple-choices moved-permanently found see-other not-modified
                   user-proxy temporary-redirect permanent-redirect])

(def client-errors [bad-request unauthorized payment-required forbidden not-found
                    method-not-allowed not-acceptable proxy-authentication-required
                    request-timeout conflict gone length-required precondition-failed
                    payload-too-large request-uri-too-long unsupported-media-type
                    requested-range-not-satisfiable expectation-failed im-a-teapot
                    misdirected-request unprocessable-entity locked failed-dependency
                    upgrade-required preconditioned-required too-many-requests
                    request-header-fields-too-large connection-closed-without-response
                    unavailable-for-legal-reasons client-closed-request])

(def server-errors [internal-server-error not-implemented bad-gateway
                    service-unavailable gateway-timeout http-version-not-supported
                    variant-also-negotiates insufficient-storage loop-detected
                    not-extended network-authentication-required
                    network-connect-timeout-error])

(def valid-statuses (vec (concat informationals successes redirections
                                 client-errors server-errors)))

(def valid-status-m (reduce (fn [a x] (assoc a x x)) nil valid-statuses))

(defn valid?
  [s]
  (contains? valid-status-m s))
