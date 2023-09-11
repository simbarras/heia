RewriteEngine on
RewriteCond %{SERVER_PORT} !^443$
RewriteRule ^/(.*) http://%{SERVER_NAME}:2000/$1 [NC,R,L]
