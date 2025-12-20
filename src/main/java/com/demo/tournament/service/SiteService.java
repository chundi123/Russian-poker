package com.demo.tournament.service;

import com.demo.tournament.entity.Site;
import com.demo.tournament.repository.SiteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SiteService {

    private final SiteRepository siteRepository;

    public SiteService(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    @Transactional
    public Site createSite(Site site) {
        site.setId(null);
        if (site.getStatus() == null) {
            site.setStatus("ACTIVE");
        }
        return siteRepository.save(site);
    }

    public List<Site> getAllSites() {
        return siteRepository.findAll();
    }

    public Optional<Site> getSiteById(Long id) {
        return siteRepository.findById(id);
    }

    public Optional<Site> getSiteByCode(String siteCode) {
        return siteRepository.findBySiteCode(siteCode);
    }

    @Transactional
    public Site updateSite(Long id, Site siteUpdate) {
        Site site = siteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Site not found with id: " + id));
        
        if (siteUpdate.getSiteName() != null) {
            site.setSiteName(siteUpdate.getSiteName());
        }
        if (siteUpdate.getStatus() != null) {
            site.setStatus(siteUpdate.getStatus());
        }
        
        return siteRepository.save(site);
    }

    @Transactional
    public void deleteSite(Long id) {
        if (!siteRepository.existsById(id)) {
            throw new IllegalArgumentException("Site not found with id: " + id);
        }
        siteRepository.deleteById(id);
    }
}

