package co.zw.blexta.checkmate.assset_code;

import co.zw.blexta.checkmate.staff_users.User;
import co.zw.blexta.checkmate.staff_users.UserRepository;
import co.zw.blexta.checkmate.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AssetCodeServiceImpl implements AssetCodeService {

    private final AssetCodeRepository repo;
    private final UserRepository userRepo;
    private final SecureRandom random = new SecureRandom();

    @Override
    public AssetCode createAssetCode(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String code;
        do {
            code = "blexta-cm-" + generateRandomToken(12);
        } while (repo.existsByCode(code));

        AssetCode assetCode = AssetCode.builder()
                .code(code)
                .createdBy(user)
                .build();

        return repo.save(assetCode);
    }

    @Override
    public AssetCode getAssetCode(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AssetCode not found"));
    }

    @Override
    public List<AssetCode> getAllAssetCodes() {
        return repo.findAll();
    }

    @Override
    public AssetCode updateAssetCode(Long id, String newCode) {
        AssetCode assetCode = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AssetCode not found"));

        if (repo.existsByCode(newCode) && !assetCode.getCode().equals(newCode)) {
            throw new IllegalArgumentException("Code already exists");
        }

        assetCode.setCode(newCode);
        return repo.save(assetCode);
    }

    @Override
    public void deleteAssetCode(Long id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("AssetCode not found");
        }
        repo.deleteById(id);
    }

    private String generateRandomToken(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
