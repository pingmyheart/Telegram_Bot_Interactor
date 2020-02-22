<?php
    $botToken="1057667995:AAFRw6YsU1OdKRvRwU1Yz_BGZ-bCkcstPjc";
    $mainApi="https://api.telegram.org/bot".$botToken;
    $chat_id="-352711975";
?>

<form action="<?php echo $mainApi.'/sendDocument' ?>" method="POST" enctype="multipart/form-data">
    <input type="hidde" nname="chat_id" value="<?php echo $chat_id ?>" />
    <input type="file" name="document" />
    <input type="submit" value="Send Document" />
</form>