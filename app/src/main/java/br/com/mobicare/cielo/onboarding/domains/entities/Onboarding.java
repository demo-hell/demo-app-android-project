package br.com.mobicare.cielo.onboarding.domains.entities;

import java.util.List;

/**
 * Created by gustavon on 23/10/17.
 */

public class Onboarding {

    private List<Pages> pages;

    public List<Pages> getPages() {
        return pages;
    }

    public void setPages(List<Pages> pages) {
        this.pages = pages;
    }

    public static class Pages {
        /**
         * title : Acompanhe suas vendas
         * subtitle : Acesse seu extrato em tempo real e consulte suas vendas. Você ainda pode visualizar e enviar os seus comprovantes para seus clientes.
         */

        private String title;
        private String subtitle;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public void setSubtitle(String subtitle) {
            this.subtitle = subtitle;
        }
    }
}
