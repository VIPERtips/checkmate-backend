package co.zw.blexta.checkmate.assset_code;

import java.util.List;

public interface AssetCodeService {

    AssetCode createAssetCode(Long userId);

    AssetCode getAssetCode(Long id);

    List<AssetCode> getAllAssetCodes();

    AssetCode updateAssetCode(Long id, String newCode);

    void deleteAssetCode(Long id);
}
