<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.payroll.util.Branding" %>
    </div><!-- end page-content -->

    <footer class="text-center text-muted py-3 border-top mt-4" style="font-size:0.8rem;">
        &copy; 2026 <%= Branding.COMPANY_NAME %>. All rights reserved.
    </footer>

</div><!-- end main-content -->

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    (function () {
        const currentPath = window.location.pathname.replace(/\/$/, '');
        document.querySelectorAll('.sidebar .nav-link[href]').forEach(function (link) {
            const linkPath = new URL(link.href, window.location.origin).pathname.replace(/\/$/, '');
            if (linkPath && currentPath === linkPath) {
                link.classList.add('active');
            }
        });
    })();
</script>
</body>
</html>
